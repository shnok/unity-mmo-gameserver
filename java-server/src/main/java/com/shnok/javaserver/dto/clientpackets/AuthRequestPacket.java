package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;
import lombok.Getter;

@Getter
public class AuthRequestPacket extends ClientPacket {

    private final String username;

    public AuthRequestPacket(byte[] data) {
        super(data);

        username = readS();
    }
}
