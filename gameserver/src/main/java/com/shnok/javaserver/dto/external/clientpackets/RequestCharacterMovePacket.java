package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import com.shnok.javaserver.model.Point3D;

public class RequestCharacterMovePacket extends ReceivablePacket {
    private final Point3D newPos = new Point3D();

    public RequestCharacterMovePacket(byte[] data) {
        super(data);
        newPos.setX(readF());
        newPos.setY(readF());
        newPos.setZ(readF());
    }

    public Point3D getPosition() {
        return newPos;
    }
}
