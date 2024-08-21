package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.ObjectAnimationPacket;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestCharacterAnimationPacket extends ClientPacket {
    private final byte animId;
    private final float value;

    public RequestCharacterAnimationPacket(GameClientThread client, byte[] data) {
        super(client, data);
        animId = readB();
        value = readF();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        // Notify known list
        ObjectAnimationPacket objectAnimationPacket = new ObjectAnimationPacket(
                client.getCurrentPlayer().getId(), getAnimId(), getValue());
        client.getCurrentPlayer().broadcastPacket(objectAnimationPacket);
    }
}
