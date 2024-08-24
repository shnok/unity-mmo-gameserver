package com.shnok.javaserver.dto.external.clientpackets.shortcut;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.thread.GameClientThread;

public class RequestShortcutDelPacket  extends ClientPacket {

    public RequestShortcutDelPacket(GameClientThread client, byte[] data) {
        super(client, data);

        handlePacket();
    }

    @Override
    public void handlePacket() {
    }
}