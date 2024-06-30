package com.shnok.javaserver.security;

import java.io.IOException;

public class LoginCrypt {
    private static final byte[] STATIC_BLOWFISH_KEY = {
            (byte) 0x6b,
            (byte) 0x60,
            (byte) 0xcb,
            (byte) 0x5b,
            (byte) 0x82,
            (byte) 0xce,
            (byte) 0x90,
            (byte) 0xb1,
            (byte) 0xcc,
            (byte) 0x2b,
            (byte) 0x6c,
            (byte) 0x55,
            (byte) 0x6c,
            (byte) 0x6c,
            (byte) 0x6c,
            (byte) 0x6c
    };

    private static final NewCrypt _STATIC_CRYPT = new NewCrypt(STATIC_BLOWFISH_KEY);
    private NewCrypt _crypt = null;
    private boolean _static = true;

    /**
     * Method to initialize the blowfish cipher with dynamic key.
     * @param key the blowfish key to initialize the dynamic blowfish cipher with
     */
    public void setKey(byte[] key) {
        _crypt = new NewCrypt(key);
    }

    /**
     * Method to decrypt an incoming login client packet.
     * @param raw array with encrypted data
     * @param offset offset where the encrypted data is located
     * @param size number of bytes of encrypted data
     * @return true when checksum could be verified, false otherwise
     * @throws IOException the size is not multiple of blowfish block size or the raw array can't hold size bytes starting at offset due to its size
     */
    public boolean decrypt(byte[] raw, final int offset, final int size) throws IOException {
        if ((size % 8) != 0) {
            throw new IOException("size have to be multiple of 8");
        }
        if ((offset + size) > raw.length) {
            throw new IOException("raw array too short for size starting from offset");
        }

        _crypt.decrypt(raw, offset, size);
        return NewCrypt.verifyChecksum(raw, offset, size);
    }

    /**
     * Method to encrypt an outgoing packet to login client.<br>
     * Performs padding and resizing of data array.
     * @param raw array with plain data
     * @param offset offset where the plain data is located
     * @param size number of bytes of plain data
     * @return the new array size
     * @throws IOException packet is too long to make padding and add verification data
     */
    public int encrypt(byte[] raw, final int offset, int size) throws IOException {
        // reserve checksum
        size += 4;

        if (_static) {
            // reserve for XOR "key"
            size += 4;

            // padding
            size += 8 - (size % 8);
            if ((offset + size) > raw.length) {
                throw new IOException("packet too long");
            }
            NewCrypt.encXORPass(raw, offset, size, Rnd.nextInt());
            _STATIC_CRYPT.crypt(raw, offset, size);
            _static = false;
        } else {
            // padding
            size += 8 - (size % 8);
            if ((offset + size) > raw.length) {
                throw new IOException("packet too long");
            }
            NewCrypt.appendChecksum(raw, offset, size);
            _crypt.crypt(raw, offset, size);
        }
        return size;
    }
}

