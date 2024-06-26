package com.shnok.javaserver.util;

import com.shnok.javaserver.security.Rnd;

import java.math.BigInteger;
import java.util.Arrays;

public class HexUtils {
    public static byte[] generateHex(int size) {
        byte[] array = new byte[size];
        Rnd.nextBytes(array);
        return array;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0'); // Pad with leading zero if necessary
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] toUnsignedByteArray(BigInteger value) {
        byte[] signedValue = value.toByteArray();
        if(signedValue[0] != 0x00) {
            throw new IllegalArgumentException("value must be a positive BigInteger");
        }
        return Arrays.copyOfRange(signedValue, 1, signedValue.length);
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return byteArray;
    }
}