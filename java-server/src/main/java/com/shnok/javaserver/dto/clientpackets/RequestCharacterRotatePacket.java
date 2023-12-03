package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;

public class RequestCharacterRotatePacket extends ClientPacket {
    private final float _angle;

    public RequestCharacterRotatePacket(byte[] data) {
        super(data);
        _angle = readF();
    }

    public float getAngle() {
        return _angle;
    }
}