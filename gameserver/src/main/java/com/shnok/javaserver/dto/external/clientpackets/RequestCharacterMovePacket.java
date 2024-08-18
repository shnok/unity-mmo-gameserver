package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestCharacterMovePacket extends ClientPacket {
    private final Point3D position = new Point3D();

    public RequestCharacterMovePacket(GameClientThread client, byte[] data) {
        super(client, data);
        position.setX(readF());
        position.setY(readF());
        position.setZ(readF());

        handlePacket();
    }

    @Override
    public void handlePacket() {
        Point3D newPos = getPosition();

        PlayerInstance currentPlayer = client.getCurrentPlayer();
        currentPlayer.setPosition(newPos);
    }
}
