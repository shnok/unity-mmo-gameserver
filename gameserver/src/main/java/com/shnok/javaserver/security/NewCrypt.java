package com.shnok.javaserver.security;

public final class NewCrypt {
    private final BlowfishEngine _cipher;

    public NewCrypt(byte[] blowfishKey) {
        this._cipher = new BlowfishEngine();
        this._cipher.init(blowfishKey);
    }

    public NewCrypt(String key) {
        this(key.getBytes());
    }

    public static boolean verifyChecksum(byte[] raw) {
        return verifyChecksum(raw, 0, raw.length);
    }

    public static boolean verifyChecksum(byte[] raw, int offset, int size) {
        if ((size & 3) == 0 && size > 4) {
            long chksum = 0L;
            int count = size - 4;

            long check;
            int i;
            for(i = offset; i < count; i += 4) {
                check = (long)(raw[i] & 255);
                check |= (long)(raw[i + 1] << 8 & '\uff00');
                check |= (long)(raw[i + 2] << 16 & 16711680);
                check |= (long)(raw[i + 3] << 24 & -16777216);
                chksum ^= check;
            }

            check = (long)(raw[i] & 255);
            check |= (long)(raw[i + 1] << 8 & '\uff00');
            check |= (long)(raw[i + 2] << 16 & 16711680);
            check |= (long)(raw[i + 3] << 24 & -16777216);
            return check == chksum;
        } else {
            return false;
        }
    }

    public static void appendChecksum(byte[] raw) {
        appendChecksum(raw, 0, raw.length);
    }

    public static void appendChecksum(byte[] raw, int offset, int size) {
        long chksum = 0L;
        int count = size - 4;

        long ecx;
        int i;
        for(i = offset; i < count; i += 4) {
            ecx = (long)(raw[i] & 255);
            ecx |= (long)(raw[i + 1] << 8 & '\uff00');
            ecx |= (long)(raw[i + 2] << 16 & 16711680);
            ecx |= (long)(raw[i + 3] << 24 & -16777216);
            chksum ^= ecx;
        }

        ecx = (long)(raw[i] & 255);
        ecx |= (long)(raw[i + 1] << 8 & '\uff00');
        ecx |= (long)(raw[i + 2] << 16 & 16711680);
        long var10000 = ecx | (long)(raw[i + 3] << 24 & -16777216);
        raw[i] = (byte)((int)(chksum & 255L));
        raw[i + 1] = (byte)((int)(chksum >> 8 & 255L));
        raw[i + 2] = (byte)((int)(chksum >> 16 & 255L));
        raw[i + 3] = (byte)((int)(chksum >> 24 & 255L));
    }

    public static void encXORPass(byte[] raw, int key) {
        encXORPass(raw, 0, raw.length, key);
    }

    public static void encXORPass(byte[] raw, int offset, int size, int key) {
        int stop = size - 8;
        int pos = 4 + offset;

        int edx;
        int ecx;
        for(ecx = key; pos < stop; raw[pos++] = (byte)(edx >> 24 & 255)) {
            edx = raw[pos] & 255;
            edx |= (raw[pos + 1] & 255) << 8;
            edx |= (raw[pos + 2] & 255) << 16;
            edx |= (raw[pos + 3] & 255) << 24;
            ecx += edx;
            edx ^= ecx;
            raw[pos++] = (byte)(edx & 255);
            raw[pos++] = (byte)(edx >> 8 & 255);
            raw[pos++] = (byte)(edx >> 16 & 255);
        }

        raw[pos++] = (byte)(ecx & 255);
        raw[pos++] = (byte)(ecx >> 8 & 255);
        raw[pos++] = (byte)(ecx >> 16 & 255);
        raw[pos] = (byte)(ecx >> 24 & 255);
    }

    public void decrypt(byte[] raw, int offset, int size) {
        for(int i = offset; i < offset + size; i += 8) {
            this._cipher.decryptBlock(raw, i);
        }

    }

    public void crypt(byte[] raw, int offset, int size) {
        for(int i = offset; i < offset + size; i += 8) {
            this._cipher.encryptBlock(raw, i);
        }

    }
}
