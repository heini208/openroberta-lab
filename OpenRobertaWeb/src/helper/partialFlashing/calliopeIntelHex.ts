export class FlashPage {
    constructor(
        readonly targetAddr: number,
        readonly data: Uint8Array,
        readonly mask: Uint8Array
    ) {}
}

export interface IntelHexParseOptions {
    universalTargetIds?: number[];
    allowPlainHex?: boolean;
}

export interface IntelHexImage {
    pages: FlashPage[];
    minAddress: number;
    maxAddress: number;
    dataBytes: number;
    isUniversalHex: boolean;
    selectedUniversalTargetId?: number;
    universalTargetIds: number[];
    hasByte(address: number): boolean;
    readByte(address: number): number | undefined;
    readBytes(address: number, length: number): Uint8Array | undefined;
    readBytes(address: number, length: number, fill: number): Uint8Array;
    containsRange(address: number, length: number): boolean;
}

function parseByte(line: string, offset: number): number {
    const value = parseInt(line.substring(offset, offset + 2), 16);

    if (Number.isNaN(value)) {
        throw new Error('Invalid Intel HEX byte: ' + line);
    }

    return value;
}

function parseWord(line: string, offset: number): number {
    return (parseByte(line, offset) << 8) | parseByte(line, offset + 2);
}

function validateLine(line: string): void {
    if (!line.startsWith(':')) {
        throw new Error('Invalid Intel HEX record: ' + line);
    }

    const length = parseByte(line, 1);
    const expectedLength = 11 + length * 2;

    if (line.length < expectedLength) {
        throw new Error('Truncated Intel HEX record: ' + line);
    }

    let sum = 0;

    for (let i = 1; i < expectedLength; i += 2) {
        sum = (sum + parseByte(line, i)) & 0xff;
    }

    if (sum !== 0) {
        throw new Error('Intel HEX checksum mismatch: ' + line);
    }
}

function getRecordType(line: string): number {
    return parseByte(line, 7);
}

function getRecordLength(line: string): number {
    return parseByte(line, 1);
}

function getOrCreatePage(
    pages: Map<number, { data: Uint8Array; mask: Uint8Array }>,
    pageAddress: number,
    pageSize: number
): { data: Uint8Array; mask: Uint8Array } {
    let page = pages.get(pageAddress);

    if (!page) {
        page = {
            data: new Uint8Array(pageSize).fill(0xff),
            mask: new Uint8Array(pageSize)
        };

        pages.set(pageAddress, page);
    }

    return page;
}

function normaliseFill(fill: number): number {
    if (!Number.isFinite(fill)) {
        return 0xff;
    }

    return fill & 0xff;
}

function hasUniversalTargetBlocks(lines: string[]): boolean {
    for (const line of lines) {
        if (getRecordType(line) === 0x0a) {
            return true;
        }
    }

    return false;
}

function parseUniversalTargetId(line: string): number | undefined {
    const length = getRecordLength(line);

    if (length >= 4) {
        const b0 = parseByte(line, 9);
        const b1 = parseByte(line, 11);
        const b2 = parseByte(line, 13);
        const b3 = parseByte(line, 15);
        const highFirst = ((b0 << 24) | (b1 << 16) | (b2 << 8) | b3) >>> 0;
        const lowFirst = ((b3 << 24) | (b2 << 16) | (b1 << 8) | b0) >>> 0;

        if ((highFirst & 0xffff) === highFirst) {
            return highFirst;
        }

        if ((lowFirst & 0xffff) === lowFirst) {
            return lowFirst;
        }

        return highFirst;
    }

    if (length >= 2) {
        return (parseByte(line, 9) << 8) | parseByte(line, 11);
    }

    return undefined;
}

function formatHex(value: number): string {
    return '0x' + value.toString(16);
}

export function parseIntelHexImage(hex: string, pageSize: number, options: IntelHexParseOptions = {}): IntelHexImage {
    const targetIds = options.universalTargetIds || [0x9903];
    const allowPlainHex = options.allowPlainHex !== false;

    const lines = hex
        .replace(/\r/g, '')
        .split('\n')
        .map((line) => line.trim())
        .filter((line) => line.length > 0);

    for (const line of lines) {
        validateLine(line);
    }

    const isUniversalHex = hasUniversalTargetBlocks(lines);
    const pages = new Map<number, { data: Uint8Array; mask: Uint8Array }>();
    const bytes = new Map<number, number>();
    const universalTargetIds: number[] = [];

    let selectedUniversalTargetId: number | undefined = undefined;
    let selectedBlock = !isUniversalHex;
    let upperAddress = 0;
    let minAddress = Number.MAX_SAFE_INTEGER;
    let maxAddress = 0;
    let dataBytes = 0;

    const addDataByte = (targetAddress: number, value: number) => {
        if (targetAddress >= 0x10000000) {
            return;
        }

        const pageAddress = targetAddress - (targetAddress % pageSize);
        const page = getOrCreatePage(pages, pageAddress, pageSize);
        const offset = targetAddress - pageAddress;

        page.data[offset] = value;
        page.mask[offset] = 1;
        bytes.set(targetAddress, value);

        minAddress = Math.min(minAddress, targetAddress);
        maxAddress = Math.max(maxAddress, targetAddress + 1);
        dataBytes++;
    };

    for (const line of lines) {
        const length = getRecordLength(line);
        const address = parseWord(line, 3);
        const type = getRecordType(line);

        if (type === 0x01) {
            break;
        }

        if (type === 0x02) {
            upperAddress = parseWord(line, 9) << 4;
            continue;
        }

        if (type === 0x04) {
            upperAddress = parseWord(line, 9) << 16;
            continue;
        }

        if (type === 0x0a) {
            const targetId = parseUniversalTargetId(line);

            selectedBlock = false;

            if (targetId !== undefined) {
                if (universalTargetIds.indexOf(targetId) === -1) {
                    universalTargetIds.push(targetId);
                }

                if (targetIds.indexOf(targetId) !== -1) {
                    selectedBlock = true;
                    selectedUniversalTargetId = targetId;
                }
            }

            continue;
        }

        if (type === 0x0b) {
            selectedBlock = false;
            continue;
        }

        if (type === 0x0c || type === 0x0e) {
            continue;
        }

        if (type !== 0x00 && type !== 0x0d) {
            continue;
        }

        if (isUniversalHex) {
            if (!selectedBlock) {
                continue;
            }
        } else if (!allowPlainHex) {
            continue;
        }

        for (let i = 0; i < length; i++) {
            addDataByte(upperAddress + address + i, parseByte(line, 9 + i * 2));
        }
    }

    if (isUniversalHex && selectedUniversalTargetId === undefined) {
        throw new Error(
            'Universal HEX does not contain a supported target. Wanted ' +
            targetIds.map(formatHex).join(', ') +
            ', found ' +
            universalTargetIds.map(formatHex).join(', ')
        );
    }

    const imagePages = Array.from(pages.entries())
        .sort((a, b) => a[0] - b[0])
        .map(([address, page]) => new FlashPage(address, page.data, page.mask));

    return {
        pages: imagePages,
        minAddress: minAddress === Number.MAX_SAFE_INTEGER ? 0 : minAddress,
        maxAddress,
        dataBytes,
        isUniversalHex,
        selectedUniversalTargetId,
        universalTargetIds,
        hasByte(address: number): boolean {
            return bytes.has(address);
        },
        readByte(address: number): number | undefined {
            return bytes.get(address);
        },
        readBytes(address: number, length: number, fill?: number): Uint8Array | undefined {
            const data = new Uint8Array(length);
            const hasFill = fill !== undefined;
            const filler = hasFill ? normaliseFill(fill) : 0;

            for (let i = 0; i < length; i++) {
                const value = bytes.get(address + i);

                if (value === undefined) {
                    if (!hasFill) {
                        return undefined;
                    }

                    data[i] = filler;
                } else {
                    data[i] = value;
                }
            }

            return data;
        },
        containsRange(address: number, length: number): boolean {
            for (let i = 0; i < length; i++) {
                if (!bytes.has(address + i)) {
                    return false;
                }
            }

            return true;
        }
    };
}

export function parseIntelHexToPages(hex: string, pageSize: number): FlashPage[] {
    return parseIntelHexImage(hex, pageSize).pages;
}

export function pageContainsAscii(page: FlashPage, text: string): boolean {
    const needle = new TextEncoder().encode(text);

    if (needle.length === 0 || needle.length > page.data.length) {
        return false;
    }

    for (let i = 0; i <= page.data.length - needle.length; i++) {
        let found = true;

        for (let j = 0; j < needle.length; j++) {
            if (page.data[i + j] !== needle[j]) {
                found = false;
                break;
            }
        }

        if (found) {
            return true;
        }
    }

    return false;
}

export function maskedByteCount(page: FlashPage): number {
    let count = 0;

    for (let i = 0; i < page.mask.length; i++) {
        if (page.mask[i]) {
            count++;
        }
    }

    return count;
}