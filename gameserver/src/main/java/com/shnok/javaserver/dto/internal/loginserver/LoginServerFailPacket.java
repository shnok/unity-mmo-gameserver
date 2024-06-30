package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class LoginServerFailPacket extends ReceivablePacket {
    private final int failReason;
    public LoginServerFailPacket(byte[] data) {
        super(data);

        readB();
        readB();
        failReason = readI();
    }
}
