package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestDropItemPacket extends ClientPacket {
    private final int itemObjectId;
    private final int count;

    public RequestDropItemPacket(GameClientThread client, byte[] data) {
        super(client, data);
        itemObjectId = readI();
        count = readI();
    }

    @Override
    public void handlePacket() {

    }
}