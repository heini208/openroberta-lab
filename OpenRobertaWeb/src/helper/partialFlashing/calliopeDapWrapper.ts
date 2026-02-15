import { CortexM, DAPLink, WebUSB } from 'dapjs';
import { apReg, bufferConcat, CoreRegister, regRequest } from 'partialFlashingUtils';
import { ApReg, CortexSpecialReg, Csw, DapCmd, DapVal, FICR } from 'microbitConstants';

const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export class CalliopeDAPWrapper {
    transport: WebUSB;
    daplink: DAPLink;
    cortexM: CortexM;

    _pageSize: number | undefined;
    _numPages: number | undefined;

    private initialConnectionComplete: boolean = false;

    constructor(public device: USBDevice) {
        this.transport = new WebUSB(this.device);
        this.daplink = new DAPLink(this.transport);
        this.cortexM = new CortexM(this.transport);
    }

    get pageSize(): number {
        if (this._pageSize === undefined) {
            throw new Error('pageSize not defined until connected');
        }
        return this._pageSize;
    }

    get numPages() {
        if (this._numPages === undefined) {
            throw new Error('numPages not defined until connected');
        }
        return this._numPages;
    }

    get flashSize(): number {
        return this.pageSize * this.numPages;
    }

    async resetAndHaltBestEffort(): Promise<void> {
        let demcr: number | undefined = undefined;

        try {
            await this.cortexM.halt(true);
        } catch (e) {
            console.warn('CALLIOPE PREPARE: initial halt failed', e);
        }

        try {
            demcr = await this.cortexM.readMem32(CortexSpecialReg.DEMCR);
            await this.cortexM.writeMem32(
                CortexSpecialReg.DEMCR,
                demcr | CortexSpecialReg.DEMCR_VC_CORERESET
            );
        } catch (e) {
            console.warn('CALLIOPE PREPARE: could not set halt-on-reset', e);
        }

        try {
            await this.cortexM.writeMem32(
                CortexSpecialReg.NVIC_AIRCR,
                CortexSpecialReg.NVIC_AIRCR_VECTKEY |
                CortexSpecialReg.NVIC_AIRCR_SYSRESETREQ
            );
        } catch (e) {
            console.warn('CALLIOPE PREPARE: AIRCR reset failed', e);
        }

        await sleep(500);

        try {
            await this.cortexM.halt(true);
        } catch (e) {
            console.warn('CALLIOPE PREPARE: halt after reset failed', e);
        }

        if (demcr !== undefined) {
            try {
                await this.cortexM.writeMem32(CortexSpecialReg.DEMCR, demcr);
            } catch (e) {
                console.warn('CALLIOPE PREPARE: could not restore DEMCR', e);
            }
        }
    }

    getBoardDebugInfo() {
        return {
            serialNumber: this.device.serialNumber,
            productName: this.device.productName,
            manufacturerName: this.device.manufacturerName,
            pageSize: this._pageSize,
            numPages: this._numPages,
            flashSize: this._pageSize !== undefined && this._numPages !== undefined ? this._pageSize * this._numPages : undefined
        };
    }

    async resetAndRunBestEffort(): Promise<void> {
        try {
            const demcr = await this.cortexM.readMem32(CortexSpecialReg.DEMCR);
            await this.cortexM.writeMem32(
                CortexSpecialReg.DEMCR,
                demcr & ~CortexSpecialReg.DEMCR_VC_CORERESET
            );
        } catch (e) {
            console.warn('CALLIOPE RESET: could not clear halt-on-reset', e);
        }

        try {
            await this.cortexM.resume(false);
        } catch (e) {
            console.warn('CALLIOPE RESET: resume before reset failed', e);
        }

        try {
            await this.cortexM.writeMem32(
                CortexSpecialReg.NVIC_AIRCR,
                CortexSpecialReg.NVIC_AIRCR_VECTKEY |
                CortexSpecialReg.NVIC_AIRCR_SYSRESETREQ
            );
        } catch (e) {
            console.warn('CALLIOPE RESET: AIRCR reset write failed', e);
        }

        await sleep(700);

        try {
            await this.cortexM.resume(false);
        } catch (e) {
            console.warn('CALLIOPE RESET: resume after reset failed', e);
        }
    }

    async reconnectAsync(): Promise<void> {
        if (this.initialConnectionComplete) {
            await this.disconnectAsync();

            this.transport = new WebUSB(this.device);
            this.daplink = new DAPLink(this.transport);
            this.cortexM = new CortexM(this.transport);
        } else {
            this.initialConnectionComplete = true;
        }

        await this.daplink.connect();
        await this.cortexM.connect();

        this._pageSize = await this.cortexM.readMem32(FICR.CODEPAGESIZE);
        this._numPages = await this.cortexM.readMem32(FICR.CODESIZE);
    }

    async forceReconnectAsync(): Promise<void> {
        try {
            await this.disconnectAsync();
        } catch (e) {
        }

        await sleep(250);

        this.transport = new WebUSB(this.device);
        this.daplink = new DAPLink(this.transport);
        this.cortexM = new CortexM(this.transport);

        await this.daplink.connect();
        await this.cortexM.connect();

        this._pageSize = await this.cortexM.readMem32(FICR.CODEPAGESIZE);
        this._numPages = await this.cortexM.readMem32(FICR.CODESIZE);
        this.initialConnectionComplete = true;
    }

    async startSerial(listener: (data: string) => void): Promise<void> {
        const currentBaud = await this.daplink.getSerialBaudrate();
        if (currentBaud !== 115200) {
            await this.daplink.setSerialBaudrate(115200);
        }
        this.daplink.on(DAPLink.EVENT_SERIAL_DATA, listener);
        await this.daplink.startSerialRead(1);
    }

    stopSerial(listener: (data: string) => void): void {
        this.daplink.stopSerialRead();
        this.daplink.removeListener(DAPLink.EVENT_SERIAL_DATA, listener);
    }

    async disconnectAsync(): Promise<void> {
        if (
            this.device.opened &&
            (this.transport as any).interfaceNumber !== undefined
        ) {
            return this.daplink.disconnect();
        }
    }

    private async send(packet: number[]): Promise<Uint8Array> {
        const array = Uint8Array.from(packet);
        await this.transport.write(array.buffer);

        const response = await this.transport.read();
        return new Uint8Array(response.buffer);
    }

    private async cmdNums(
        op: number,
        data: number[]
    ): Promise<Uint8Array> {
        data.unshift(op);

        const buf = await this.send(data);

        if (buf[0] !== op) {
            throw new Error(`Bad response for ${op} -> ${buf[0]}`);
        }

        switch (op) {
            case DapCmd.DAP_CONNECT:
            case DapCmd.DAP_INFO:
            case DapCmd.DAP_TRANSFER:
            case DapCmd.DAP_TRANSFER_BLOCK:
                break;
            default:
                if (buf[1] !== 0) {
                    throw new Error(`Bad status for ${op} -> ${buf[1]}`);
                }
        }

        return buf;
    }

    private async readRegRepeat(
        regId: number,
        cnt: number
    ): Promise<Uint8Array> {
        const request = regRequest(regId);
        const sendargs = [0, cnt];

        for (let i = 0; i < cnt; ++i) {
            sendargs.push(request);
        }

        const buf = await this.cmdNums(DapCmd.DAP_TRANSFER, sendargs);

        if (buf[1] !== cnt) {
            throw new Error('(many) Bad #trans ' + buf[1]);
        } else if (buf[2] !== 1) {
            throw new Error('(many) Bad transfer status ' + buf[2]);
        }

        return buf.subarray(3, 3 + cnt * 4);
    }

    private async writeRegRepeat(
        regId: number,
        data: Uint32Array
    ): Promise<void> {
        const request = regRequest(regId, true);
        const sendargs = [0, data.length, 0, request];

        data.forEach((d) => {
            sendargs.push(
                d & 0xff,
                (d >> 8) & 0xff,
                (d >> 16) & 0xff,
                (d >> 24) & 0xff
            );
        });

        const buf = await this.cmdNums(DapCmd.DAP_TRANSFER_BLOCK, sendargs);

        if (buf[3] !== 1) {
            throw new Error('(many-wr) Bad transfer status ' + buf[2]);
        }
    }

    private async readBlockCore(
        addr: number,
        words: number
    ): Promise<Uint8Array> {
        await this.cortexM.writeAP(ApReg.CSW, Csw.CSW_VALUE | Csw.CSW_SIZE32);
        await this.cortexM.writeAP(ApReg.TAR, addr);

        const blocks: Uint8Array[] = [];
        const blockCount = Math.ceil(words / 15);

        for (let i = 0; i < blockCount; i++) {
            const remaining = words - i * 15;
            const count = Math.min(15, remaining);
            const block = await this.readRegRepeat(
                apReg(ApReg.DRW, DapVal.READ),
                count
            );
            blocks.push(block);
        }

        return bufferConcat(blocks).subarray(0, words * 4);
    }

    private async writeBlockCore(
        addr: number,
        words: Uint32Array
    ): Promise<void> {
        try {
            await this.cortexM.writeAP(ApReg.CSW, Csw.CSW_VALUE | Csw.CSW_SIZE32);
            await this.cortexM.writeAP(ApReg.TAR, addr);
            await this.writeRegRepeat(apReg(ApReg.DRW, DapVal.WRITE), words);
        } catch (e: any) {
            if (e.dapWait) {
                await sleep(100);
                return await this.writeBlockCore(addr, words);
            }
            throw e;
        }
    }

    async readBlockAsync(addr: number, words: number): Promise<Uint8Array> {
        const bufs: Uint8Array[] = [];
        const end = addr + words * 4;
        let ptr = addr;

        while (ptr < end) {
            let nextptr = ptr + this.pageSize;
            if (ptr === addr) {
                nextptr &= ~(this.pageSize - 1);
                if (nextptr <= ptr) {
                    nextptr = ptr + this.pageSize;
                }
            }
            const len = Math.min(nextptr - ptr, end - ptr);
            bufs.push(await this.readBlockCore(ptr, len >> 2));
            ptr += len;
        }

        const result = bufferConcat(bufs);
        return result.subarray(0, words * 4);
    }

    async writeBlockAsync(address: number, data: Uint32Array): Promise<void> {
        const payloadBytes = Math.max(4, Math.floor((this.transport.packetSize - 8) / 4) * 4);
        const payloadWords = payloadBytes >> 2;

        for (let start = 0; start < data.length; start += payloadWords) {
            const temp = data.subarray(start, Math.min(data.length, start + payloadWords));
            await this.writeBlockCore(address + start * 4, temp);
        }
    }

    async executeAsync(
        address: number,
        code: Uint32Array,
        sp: number,
        pc: number,
        lr: number,
        ...registers: number[]
    ) {
        if (registers.length > 12) {
            throw new Error(
                `Only 12 general purpose registers but got ${registers.length} values`
            );
        }

        await this.cortexM.halt(true);
        await this.writeBlockAsync(address, code);
        await this.cortexM.writeCoreRegister(CoreRegister.PC, pc);
        await this.cortexM.writeCoreRegister(CoreRegister.LR, lr);
        await this.cortexM.writeCoreRegister(CoreRegister.SP, sp);
        for (let i = 0; i < registers.length; ++i) {
            await this.cortexM.writeCoreRegister(i, registers[i]);
        }
        await this.cortexM.resume(true);
        return this.waitForHalt(30000);
    }

    async waitForHalt(timeToWait = 10000): Promise<void> {
        const deadline = Date.now() + timeToWait;

        while (Date.now() <= deadline) {
            if (await this.cortexM.isHalted()) {
                return;
            }
            await sleep(20);
        }

        throw new Error('timeout waiting for target halt');
    }

    private async softwareReset() {
        await this.cortexM.writeMem32(
            CortexSpecialReg.NVIC_AIRCR,
            CortexSpecialReg.NVIC_AIRCR_VECTKEY |
            CortexSpecialReg.NVIC_AIRCR_SYSRESETREQ
        );

        let dhcsr = await this.cortexM.readMem32(CortexSpecialReg.DHCSR);

        while ((dhcsr & CortexSpecialReg.S_RESET_ST) !== 0) {
            await sleep(10);
            dhcsr = await this.cortexM.readMem32(CortexSpecialReg.DHCSR);
        }
    }

    async reset(halt = false) {
        if (halt) {
            await this.cortexM.halt(true);

            const demcr = await this.cortexM.readMem32(CortexSpecialReg.DEMCR);

            await this.cortexM.writeMem32(
                CortexSpecialReg.DEMCR,
                demcr | CortexSpecialReg.DEMCR_VC_CORERESET
            );

            await this.softwareReset();
            await this.waitForHalt();

            await this.cortexM.writeMem32(CortexSpecialReg.DEMCR, demcr);
        } else {
            await this.softwareReset();
        }
    }
}
