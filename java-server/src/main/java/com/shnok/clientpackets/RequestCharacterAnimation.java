package com.shnok.clientpackets;

import com.shnok.model.Point3D;

public class RequestCharacterAnimation extends ClientPacket {
    private byte animId;
    private float value;

    public RequestCharacterAnimation(byte[] data) {
        super(data);
        animId = readB();
        value = readF();
    }

    public byte getAnimId() {
        return animId;
    }

    public float getValue() {
        return value;
    }
}
