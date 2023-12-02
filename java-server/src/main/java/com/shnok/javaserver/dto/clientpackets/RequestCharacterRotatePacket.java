package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;

public class RequestCharacterRotate extends ClientPacket {
    private final float _angle;

    public RequestCharacterRotate(byte[] data) {
        super(data);
        _angle = readF();
    }

    public float getAngle() {
        return _angle;
    }
}