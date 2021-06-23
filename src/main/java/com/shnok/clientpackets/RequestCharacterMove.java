package com.shnok.clientpackets;

import com.shnok.model.Point3D;

public class RequestCharacterMove extends ClientPacket {
    private final Point3D newPos = new Point3D();

    public RequestCharacterMove(byte[] data) {
        super(data);
        newPos.setX(readI());
        newPos.setY(readI());
        newPos.setZ(readI());
    }

    public Point3D getPosition() {
        return newPos;
    }
}
