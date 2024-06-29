package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class PlayerAuthResponsePacket extends ReceivablePacket {
    private final String account;
    private final boolean authed;
    public PlayerAuthResponsePacket(byte[] data) {
        super(data);

        account = readS();
        authed = readB() != 0;
    }
}
