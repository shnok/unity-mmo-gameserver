package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class AuthResponsePacket extends ReceivablePacket {
    private final int id;
    public AuthResponsePacket(byte[] data) {
        super(data);

        readB();
        readB();
        id = readI();
    }
}