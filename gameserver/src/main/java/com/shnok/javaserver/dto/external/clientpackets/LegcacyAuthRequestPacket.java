package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class LegcacyAuthRequestPacket extends ReceivablePacket {

    private final String username;

    public LegcacyAuthRequestPacket(byte[] data) {
        super(data);

        username = readS();
    }
}
