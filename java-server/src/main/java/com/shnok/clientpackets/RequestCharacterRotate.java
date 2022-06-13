package com.shnok.clientpackets;

public class RequestCharacterRotate extends ClientPacket {
    private final float _angle;

    public RequestCharacterRotate(byte[] data) {
        super(data);
        _angle = readF();

        System.out.println(_angle);
    }

    public float getAngle() {
        return _angle;
    }
}