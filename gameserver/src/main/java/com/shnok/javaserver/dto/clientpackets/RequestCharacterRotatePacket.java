package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;
import lombok.Getter;

@Getter
public class RequestCharacterRotatePacket extends ClientPacket {
    private final float angle;

    public RequestCharacterRotatePacket(byte[] data) {
        super(data);
        angle = readF();
    }
}