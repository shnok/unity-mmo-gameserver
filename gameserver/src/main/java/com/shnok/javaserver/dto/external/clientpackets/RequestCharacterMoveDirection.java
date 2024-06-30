package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import com.shnok.javaserver.model.Point3D;
import lombok.Getter;

@Getter
public class RequestCharacterMoveDirection extends ReceivablePacket {
    private final Point3D direction = new Point3D();

    public RequestCharacterMoveDirection(byte[] data) {
        super(data);
        direction.setX(readF());
        direction.setY(readF());
        direction.setZ(readF());
    }
}