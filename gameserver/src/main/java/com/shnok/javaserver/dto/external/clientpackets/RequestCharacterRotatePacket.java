package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.ObjectRotationPacket;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestCharacterRotatePacket extends ClientPacket {
    private final float angle;

    public RequestCharacterRotatePacket(GameClientThread client, byte[] data) {
        super(client, data);
        angle = readF();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        // Notify known list
        ObjectRotationPacket objectRotationPacket = new ObjectRotationPacket(
                client.getCurrentPlayer().getId(), getAngle());
        client.getCurrentPlayer().broadcastPacket(objectRotationPacket);
    }
}