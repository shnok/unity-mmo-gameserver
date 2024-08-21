package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.MessagePacket;
import com.shnok.javaserver.service.GameServerController;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestSendMessagePacket extends ClientPacket {
    private final String message;

    public RequestSendMessagePacket(GameClientThread client, byte[] data) {
        super(client, data);

        message = readS();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        String message = getMessage();

        MessagePacket messagePacket = new MessagePacket(client.getAccountName(), message);
        GameServerController.getInstance().broadcast(messagePacket);
    }
}