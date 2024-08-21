package com.shnok.javaserver.dto.external;

import com.shnok.javaserver.dto.ReceivablePacket;
import com.shnok.javaserver.thread.GameClientThread;

public abstract class ClientPacket extends ReceivablePacket {
    protected GameClientThread client;

    public ClientPacket(GameClientThread client, byte[] data) {
        super(data);
        this.client = client;
    }
}