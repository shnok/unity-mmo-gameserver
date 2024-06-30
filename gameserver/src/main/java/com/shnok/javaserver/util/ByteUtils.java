package com.shnok.javaserver.util;

public class ByteUtils {
    public static int swapInt(int intValue) {
        int byte0 = ((intValue >> 24) & 0xFF);
        int byte1 = ((intValue >> 16) & 0xFF);
        int byte2 = ((intValue >> 8) & 0xFF);
        int byte3 = (intValue & 0xFF);
        return (byte3 << 24) + (byte2 << 16) + (byte1 << 8) + (byte0);
    }

    public static short swapShort(short shortValue) {
        byte byte1 = (byte) (shortValue & 0xFF);
        byte byte2 = (byte) ((shortValue >> 8) & 0xFF);

        return (short) ((byte1 << 8) | (byte2 & 0xFF));
    }
}
