package com.shnok.javaserver.dto.external.clientpackets.shortcut;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestShortcutDelPacket extends ClientPacket {
    private final int slot;
    private final int page;

    public RequestShortcutDelPacket(GameClientThread client, byte[] data) {
        super(client, data);
        int id = readI();
        slot = id % 12;
        page = id / 12;

        handlePacket();
    }

    @Override
    public void handlePacket() {
        PlayerInstance activeChar = client.getCurrentPlayer();
        if (activeChar == null) {
            return;
        }

        activeChar.deleteShortCut(slot, page);
        // client needs no confirmation. this packet is just to inform the server
    }
}