package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class InitLSPacket extends ReceivablePacket {
    private final byte[] rsaKey;
    public InitLSPacket(byte[] data) {
        super(data);

        readB();
        int rsaKeyLength = readI();
        rsaKey = readB(rsaKeyLength);
    }
}
