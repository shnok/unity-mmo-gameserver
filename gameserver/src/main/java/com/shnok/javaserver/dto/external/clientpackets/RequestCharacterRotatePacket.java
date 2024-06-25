package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestCharacterRotatePacket extends ReceivablePacket {
    private final float angle;

    public RequestCharacterRotatePacket(byte[] data) {
        super(data);
        angle = readF();
    }
}