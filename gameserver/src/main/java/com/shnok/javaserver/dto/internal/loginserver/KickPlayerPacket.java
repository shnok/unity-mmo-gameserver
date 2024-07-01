package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class KickPlayerPacket extends ReceivablePacket {
    private final String account;
    public KickPlayerPacket(byte[] data) {
        super(data);

        account = readS();
    }
}
