package com.shnok.clientpackets;

import com.shnok.model.Point3D;

public class RequestCharacterAnimation extends ClientPacket {
    private int animId;
    private float value;

    public RequestCharacterAnimation(byte[] data) {
        super(data);
        animId = readI();
        value = readF();
    }

    public int getAnimId() {
        return animId;
    }

    public float getValue() {
        return value;
    }
}
