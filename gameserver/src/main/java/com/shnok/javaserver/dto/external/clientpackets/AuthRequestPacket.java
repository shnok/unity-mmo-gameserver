package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class AuthRequestPacket extends ReceivablePacket {

    private final String username;

    public AuthRequestPacket(byte[] data) {
        super(data);

        username = readS();
    }
}
