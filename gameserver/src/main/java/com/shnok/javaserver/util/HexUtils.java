package com.shnok.javaserver.util;

import com.shnok.javaserver.security.Rnd;

public class HexUtils {
    public static byte[] generateHex(int size) {
        byte[] array = new byte[size];
        Rnd.nextBytes(array);
        return array;
    }
}