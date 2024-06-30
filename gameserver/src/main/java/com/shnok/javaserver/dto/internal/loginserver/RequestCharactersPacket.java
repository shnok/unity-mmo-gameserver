package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestCharactersPacket extends ReceivablePacket {
    private final String account;
    public RequestCharactersPacket(byte[] data) {
        super(data);

        account = readS();
    }
}
