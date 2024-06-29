package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class AuthLoginPacket extends ReceivablePacket {
    // loginName + keys must match what the login server used.
    private final String account;
    private final int playKey1;
    private final int playKey2;
    private final int loginKey1;
    private final int loginKey2;

    public AuthLoginPacket(byte[] data) {
        super(data);

        account = readS().toLowerCase();
        playKey1 = readI();
        playKey2 = readI();
        loginKey1 = readI();
        loginKey2 = readI();
    }
}
