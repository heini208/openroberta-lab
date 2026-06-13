import { DAPLink, WebUSB } from 'dapjs';
import { CalliopeDAPWrapper } from 'calliopeDapWrapper';
import { IntelHexImage, parseIntelHexImage } from 'calliopeIntelHex';
import { CoreRegister, onlyChanged, Page, read32FromUInt8Array } from 'partialFlashingUtils';

type ProgressCallback = (n: number) => void;

const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const flashPageBIN = new Uint32Array([
    0xbe00be00,
    0x2502b5f0, 0x4c204b1f, 0xf3bf511d, 0xf3bf8f6f, 0x25808f4f, 0x002e00ed,
    0x2f00595f, 0x25a1d0fc, 0x515800ed, 0x2d00599d, 0x2500d0fc, 0xf3bf511d,
    0xf3bf8f6f, 0x25808f4f, 0x002e00ed, 0x2f00595f, 0x2501d0fc, 0xf3bf511d,
    0xf3bf8f6f, 0x599d8f4f, 0xd0fc2d00, 0x25002680, 0x00f60092, 0xd1094295,
    0x511a2200, 0x8f6ff3bf, 0x8f4ff3bf, 0x2a00599a, 0xbdf0d0fc, 0x5147594f,
    0x2f00599f, 0x3504d0fc, 0x46c0e7ec, 0x4001e000, 0x00000504
]);

const computeChecksums2 = new Uint32Array([
    0x4c27b5f0, 0x44a52680, 0x22009201, 0x91004f25, 0x00769303, 0x24080013,
    0x25010019, 0x40eb4029, 0xd0002900, 0x3c01407b, 0xd1f52c00, 0x468c0091,
    0xa9044665, 0x506b3201, 0xd1eb42b2, 0x089b9b01, 0x23139302, 0x9b03469c,
    0xd104429c, 0x2000be2a, 0x449d4b15, 0x9f00bdf0, 0x4d149e02, 0x49154a14,
    0x3e01cf08, 0x2111434b, 0x491341cb, 0x405a434b, 0x4663405d, 0x230541da,
    0x4b10435a, 0x466318d2, 0x230541dd, 0x4b0d435d, 0x2e0018ed, 0x6002d1e7,
    0x9a009b01, 0x18d36045, 0x93003008, 0xe7d23401, 0xfffffbec, 0xedb88320,
    0x00000414, 0x1ec3a6c8, 0x2f9be6cc, 0xcc9e2d51, 0x1b873593, 0xe6546b64
]);

const membase = 0x20000000;
const loadAddr = membase;
const dataAddr = 0x20002000;
const stackAddr = 0x20001000;

const FLASH_LAYOUT_MAGIC1 = 0x597f30fe;
const FLASH_LAYOUT_MAGIC2 = 0xc1b1d79d;
const FLASH_LAYOUT_VERSION = 1;

const REGION_SOFTDEVICE = 1;
const REGION_MICROPYTHON = 2;
const REGION_FILESYSTEM = 3;

const MAX_LAYOUT_BYTES = 1024;
const MAX_PARTIAL_PAGES = 16;
const UNIVERSAL_HEX_TARGET_IDS = [0x9903];

interface FlashLayoutHeader {
    headerAddress: number;
    tableStart: number;
    tableLength: number;
    numRegions: number;
    pageSizeLog2: number;
    pageSize: number;
}

interface FlashLayoutRegion {
    id: number;
    hashType: number;
    page: number;
    address: number;
    length: number;
    hash: Uint8Array;
}

interface FlashLayoutTable extends FlashLayoutHeader {
    regions: FlashLayoutRegion[];
}

interface FilesystemPageResult {
    filesystem: FlashLayoutRegion;
    pages: Page[];
    pagesWithHexBytes: number;
    totalHexBytes: number;
}

export class CalliopePartialFlashing {
    constructor(private dapwrapper: CalliopeDAPWrapper) {}

    private read16(data: Uint8Array, offset: number): number {
        return data[offset] | (data[offset + 1] << 8);
    }

    private read32(data: Uint8Array, offset: number): number {
        return read32FromUInt8Array(data, offset);
    }

    private bytesToHex(data: Uint8Array): string {
        return Array.from(data)
            .map((value) => ('0' + value.toString(16)).slice(-2))
            .join('');
    }

    private bytesToWords(data: Uint8Array): Uint32Array {
        const words = new Uint32Array(data.length >> 2);

        for (let i = 0; i < data.length; i += 4) {
            words[i >> 2] = read32FromUInt8Array(data, i);
        }

        return words;
    }

    private wordToBytes(word: number, data: Uint8Array, offset: number): void {
        data[offset] = word & 0xff;
        data[offset + 1] = (word >> 8) & 0xff;
        data[offset + 2] = (word >> 16) & 0xff;
        data[offset + 3] = (word >> 24) & 0xff;
    }

    private async readWordsAsBytes(address: number, wordCount: number): Promise<Uint8Array> {
        const data = new Uint8Array(wordCount * 4);

        for (let i = 0; i < wordCount; i++) {
            const word = await this.dapwrapper.cortexM.readMem32(address + i * 4);
            this.wordToBytes(word, data, i * 4);
        }

        return data;
    }

    private async readFlashBytes(address: number, byteLength: number): Promise<Uint8Array> {
        const alignedAddress = address & ~3;
        const alignedEnd = (address + byteLength + 3) & ~3;
        const raw = await this.readWordsAsBytes(alignedAddress, (alignedEnd - alignedAddress) >> 2);
        return raw.subarray(address - alignedAddress, address - alignedAddress + byteLength);
    }

    private parseLayoutHeader(headerAddress: number, header: Uint8Array): FlashLayoutHeader | undefined {
        if (header.length !== 16) {
            return undefined;
        }

        if (this.read32(header, 0) !== FLASH_LAYOUT_MAGIC1 || this.read32(header, 12) !== FLASH_LAYOUT_MAGIC2) {
            return undefined;
        }

        const version = this.read16(header, 4);
        const tableLength = this.read16(header, 6);
        const numRegions = this.read16(header, 8);
        const pageSizeLog2 = this.read16(header, 10);
        const pageSize = 1 << pageSizeLog2;
        const tableStart = headerAddress - tableLength;

        if (version !== FLASH_LAYOUT_VERSION) {
            return undefined;
        }

        if (tableLength <= 0 || tableLength > MAX_LAYOUT_BYTES || tableLength < numRegions * 16) {
            return undefined;
        }

        if (numRegions <= 0 || numRegions > 32) {
            return undefined;
        }

        if (pageSize !== this.dapwrapper.pageSize) {
            return undefined;
        }

        if (tableStart < 0) {
            return undefined;
        }

        return {
            headerAddress,
            tableStart,
            tableLength,
            numRegions,
            pageSizeLog2,
            pageSize
        };
    }

    private parseLayoutTable(header: FlashLayoutHeader, tableBytes: Uint8Array): FlashLayoutTable | undefined {
        if (tableBytes.length < header.tableLength) {
            return undefined;
        }

        const regions: FlashLayoutRegion[] = [];

        for (let i = 0; i < header.numRegions; i++) {
            const offset = i * 16;
            const id = tableBytes[offset];
            const hashType = tableBytes[offset + 1];
            const page = this.read16(tableBytes, offset + 2);
            const length = this.read32(tableBytes, offset + 4);
            const hash = tableBytes.slice(offset + 8, offset + 16);
            const address = page * header.pageSize;

            regions.push({
                id,
                hashType,
                page,
                address,
                length,
                hash
            });
        }

        return {
            ...header,
            regions
        };
    }

    private findRegion(layout: FlashLayoutTable, id: number): FlashLayoutRegion | undefined {
        return layout.regions.find((region) => region.id === id);
    }

    private async readTargetLayout(): Promise<FlashLayoutTable | undefined> {
        for (let headerAddress = this.dapwrapper.pageSize - 16; headerAddress < this.dapwrapper.flashSize; headerAddress += this.dapwrapper.pageSize) {
            const headerBytes = await this.readFlashBytes(headerAddress, 16);
            const header = this.parseLayoutHeader(headerAddress, headerBytes);

            if (!header) {
                continue;
            }

            const tableBytes = await this.readFlashBytes(header.tableStart, header.tableLength);
            const layout = this.parseLayoutTable(header, tableBytes);

            if (layout) {
                return layout;
            }
        }

        return undefined;
    }

    private readImageLayout(image: IntelHexImage): FlashLayoutTable | undefined {
        for (let headerAddress = this.dapwrapper.pageSize - 16; headerAddress < this.dapwrapper.flashSize; headerAddress += this.dapwrapper.pageSize) {
            const headerBytes = image.readBytes(headerAddress, 16);

            if (!headerBytes) {
                continue;
            }

            const header = this.parseLayoutHeader(headerAddress, headerBytes);

            if (!header) {
                continue;
            }

            const tableBytes = image.readBytes(header.tableStart, header.tableLength);

            if (!tableBytes) {
                continue;
            }

            const layout = this.parseLayoutTable(header, tableBytes);

            if (layout) {
                return layout;
            }
        }

        return undefined;
    }

    private sameRegionIdentity(a: FlashLayoutRegion | undefined, b: FlashLayoutRegion | undefined): boolean {
        if (!a || !b) {
            return false;
        }

        return a.id === b.id &&
            a.hashType === b.hashType &&
            a.address === b.address &&
            a.length === b.length &&
            this.bytesToHex(a.hash) === this.bytesToHex(b.hash);
    }

    private compatibleLayouts(fileLayout: FlashLayoutTable, targetLayout: FlashLayoutTable): boolean {
        const fileSoftDevice = this.findRegion(fileLayout, REGION_SOFTDEVICE);
        const targetSoftDevice = this.findRegion(targetLayout, REGION_SOFTDEVICE);
        const fileMicroPython = this.findRegion(fileLayout, REGION_MICROPYTHON);
        const targetMicroPython = this.findRegion(targetLayout, REGION_MICROPYTHON);
        const fileFilesystem = this.findRegion(fileLayout, REGION_FILESYSTEM);
        const targetFilesystem = this.findRegion(targetLayout, REGION_FILESYSTEM);

        if (!this.sameRegionIdentity(fileSoftDevice, targetSoftDevice)) {
            return false;
        }

        if (!this.sameRegionIdentity(fileMicroPython, targetMicroPython)) {
            return false;
        }

        if (!fileFilesystem || !targetFilesystem) {
            return false;
        }

        return fileFilesystem.address === targetFilesystem.address &&
            fileFilesystem.length === targetFilesystem.length;
    }

    private getFallbackReason(fileLayout: FlashLayoutTable | undefined, targetLayout: FlashLayoutTable | undefined): string | undefined {
        if (!fileLayout) {
            return 'input HEX has no Calliope V3 MicroPython flash layout table in the selected target image';
        }

        if (!targetLayout) {
            return 'device has no readable Calliope V3 MicroPython flash layout table';
        }

        if (!this.compatibleLayouts(fileLayout, targetLayout)) {
            return 'device runtime does not match input HEX runtime';
        }

        return undefined;
    }

    private createFilesystemPages(image: IntelHexImage, layout: FlashLayoutTable): FilesystemPageResult {
        const filesystem = this.findRegion(layout, REGION_FILESYSTEM);

        if (!filesystem) {
            throw new Error('missing filesystem region');
        }

        if (filesystem.address % this.dapwrapper.pageSize !== 0 || filesystem.length <= 0) {
            throw new Error('invalid filesystem region');
        }

        if (filesystem.address + filesystem.length > this.dapwrapper.flashSize) {
            throw new Error('filesystem region exceeds target flash');
        }

        const pages: Page[] = [];
        let pagesWithHexBytes = 0;
        let totalHexBytes = 0;

        for (let address = filesystem.address; address < filesystem.address + filesystem.length; address += this.dapwrapper.pageSize) {
            const data = new Uint8Array(this.dapwrapper.pageSize).fill(0xff);
            let bytesFromHex = 0;

            for (let offset = 0; offset < this.dapwrapper.pageSize; offset++) {
                const value = image.readByte(address + offset);

                if (value !== undefined) {
                    data[offset] = value;
                    bytesFromHex++;
                }
            }

            if (bytesFromHex > 0) {
                pagesWithHexBytes++;
                totalHexBytes += bytesFromHex;
            }

            console.log('CALLIOPE PARTIAL FLASH: FILESYSTEM PAGE', {
                page: '0x' + address.toString(16),
                bytesFromHex
            });

            pages.push(new Page(address, data));
        }

        return {
            filesystem,
            pages,
            pagesWithHexBytes,
            totalHexBytes
        };
    }

    private async getFlashChecksumsAsync(): Promise<Uint8Array> {
        await this.dapwrapper.executeAsync(
            loadAddr,
            computeChecksums2,
            stackAddr,
            loadAddr + 1,
            0xffffffff,
            dataAddr,
            0,
            this.dapwrapper.pageSize,
            this.dapwrapper.numPages
        );

        return this.readWordsAsBytes(dataAddr, this.dapwrapper.numPages * 2);
    }

    private async readFlashPage(targetAddr: number): Promise<Uint8Array> {
        return this.readWordsAsBytes(targetAddr, this.dapwrapper.pageSize >> 2);
    }

    private pageEquals(a: Uint8Array, b: Uint8Array): boolean {
        if (a.length !== b.length) {
            return false;
        }

        for (let i = 0; i < a.length; i++) {
            if (a[i] !== b[i]) {
                return false;
            }
        }

        return true;
    }

    private async runFlash(page: Page, bufferAddress: number): Promise<void> {
        await this.dapwrapper.cortexM.halt(true);
        await this.dapwrapper.cortexM.writeCoreRegister(CoreRegister.PC, loadAddr + 4 + 1);
        await this.dapwrapper.cortexM.writeCoreRegister(CoreRegister.LR, loadAddr + 1);
        await this.dapwrapper.cortexM.writeCoreRegister(CoreRegister.SP, stackAddr);
        await this.dapwrapper.cortexM.writeCoreRegister(0, page.targetAddr);
        await this.dapwrapper.cortexM.writeCoreRegister(1, bufferAddress);
        await this.dapwrapper.cortexM.writeCoreRegister(2, this.dapwrapper.pageSize >> 2);
        await this.dapwrapper.cortexM.resume(false);
        await this.dapwrapper.waitForHalt(60000);
    }

    private async partialFlashPageAsync(page: Page, index: number): Promise<void> {
        const bufferAddress = index & 1 ? dataAddr : dataAddr + this.dapwrapper.pageSize;
        await this.dapwrapper.writeBlockAsync(bufferAddress, this.bytesToWords(page.data));
        await this.runFlash(page, bufferAddress);
    }

    private async partialFlashCoreAsync(pages: Page[], updateProgress: ProgressCallback): Promise<void> {
        for (let i = 0; i < pages.length; ++i) {
            updateProgress(i / pages.length);
            await this.partialFlashPageAsync(pages[i], i);
        }

        updateProgress(1);
    }

    private async verifyPages(pages: Page[]): Promise<void> {
        for (const page of pages) {
            const readback = await this.readFlashPage(page.targetAddr);

            if (!this.pageEquals(readback, page.data)) {
                throw new Error('verify failed at page 0x' + page.targetAddr.toString(16));
            }
        }
    }

    async flashHexAsync(hex: string, updateProgress: ProgressCallback): Promise<boolean> {
        const image = parseIntelHexImage(hex, this.dapwrapper.pageSize, {
            universalTargetIds: UNIVERSAL_HEX_TARGET_IDS,
            allowPlainHex: true
        });

        console.log('CALLIOPE PARTIAL FLASH: HEX IMAGE', {
            isUniversalHex: image.isUniversalHex,
            selectedUniversalTargetId: image.selectedUniversalTargetId !== undefined ? '0x' + image.selectedUniversalTargetId.toString(16) : undefined,
            universalTargetIds: image.universalTargetIds.map((id) => '0x' + id.toString(16)).join(', '),
            minAddress: '0x' + image.minAddress.toString(16),
            maxAddress: '0x' + image.maxAddress.toString(16),
            dataBytes: image.dataBytes,
            imagePages: image.pages.length
        });

        const fileLayout = this.readImageLayout(image);
        const targetLayout = await this.readTargetLayout();
        const fallbackReason = this.getFallbackReason(fileLayout, targetLayout);

        if (fallbackReason) {
            console.warn('CALLIOPE PARTIAL FLASH SKIPPED: ' + fallbackReason);
            await this.fullFlashAsync(hex, updateProgress);
            return false;
        }

        if (!fileLayout) {
            throw new Error('missing file layout after validation');
        }

        const filesystemResult = this.createFilesystemPages(image, fileLayout);

        console.log('CALLIOPE PARTIAL FLASH: FILESYSTEM IMAGE', {
            filesystemStart: '0x' + filesystemResult.filesystem.address.toString(16),
            filesystemLength: filesystemResult.filesystem.length,
            filesystemPages: filesystemResult.pages.length,
            pagesWithHexBytes: filesystemResult.pagesWithHexBytes,
            totalHexBytes: filesystemResult.totalHexBytes
        });

        if (filesystemResult.pagesWithHexBytes === 0) {
            console.warn('CALLIOPE PARTIAL FLASH SKIPPED: selected target image contains no filesystem bytes');
            await this.fullFlashAsync(hex, updateProgress);
            return false;
        }

        const checksums = await this.getFlashChecksumsAsync();
        const changedFilesystemPages = onlyChanged(filesystemResult.pages, checksums, this.dapwrapper.pageSize);

        console.log('CALLIOPE PARTIAL FLASH: FILESYSTEM DIFF', {
            changedFilesystemPages: changedFilesystemPages.length,
            pages: changedFilesystemPages.map((page) => '0x' + page.targetAddr.toString(16)).join(', ')
        });

        if (changedFilesystemPages.length === 0) {
            updateProgress(1);
            await this.dapwrapper.resetAndRunBestEffort();
            return true;
        }

        const pages = filesystemResult.pages;

        console.log('CALLIOPE PARTIAL FLASH: FINAL PAGE SET', {
            totalPages: pages.length,
            pages: pages.map((page) => '0x' + page.targetAddr.toString(16)).join(', ')
        });

        if (pages.length > MAX_PARTIAL_PAGES) {
            console.warn('CALLIOPE PARTIAL FLASH SKIPPED: too many filesystem pages', {
                totalPages: pages.length,
                pages: pages.map((page) => '0x' + page.targetAddr.toString(16)).join(', ')
            });

            await this.fullFlashAsync(hex, updateProgress);
            return false;
        }

        await this.dapwrapper.writeBlockAsync(loadAddr, flashPageBIN);
        await this.partialFlashCoreAsync(pages, updateProgress);
        await this.verifyPages(pages);
        await this.dapwrapper.resetAndRunBestEffort();

        console.log('CALLIOPE PARTIAL FLASH: COMPLETE');
        return true;
    }

    async fullFlashAsync(hex: string, updateProgress: ProgressCallback): Promise<void> {
        try {
            await this.dapwrapper.disconnectAsync();
        } catch (e) {
        }

        await sleep(500);

        const transport = new WebUSB(this.dapwrapper.device);
        const daplink = new DAPLink(transport);

        const fullFlashProgress = (progress: number) => {
            updateProgress(progress);
        };

        daplink.on(DAPLink.EVENT_PROGRESS, fullFlashProgress);

        try {
            const data = new TextEncoder().encode(hex);
            await daplink.connect();
            await daplink.flash(data);
        } finally {
            daplink.removeListener(DAPLink.EVENT_PROGRESS, fullFlashProgress);

            try {
                await daplink.disconnect();
            } catch (e) {
            }
        }
    }

    async flashAsync(hex: string, updateProgress: ProgressCallback): Promise<boolean> {
        try {
            await this.dapwrapper.resetAndHaltBestEffort();
            return await this.flashHexAsync(hex, updateProgress);
        } catch (e) {
            console.error('CALLIOPE PARTIAL FLASH FAILED, FALLING BACK TO FULL FLASH', e);
            await this.fullFlashAsync(hex, updateProgress);
            return false;
        }
    }
}