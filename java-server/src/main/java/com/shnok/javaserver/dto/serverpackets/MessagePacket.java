package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class MessagePacket extends ServerPacket {
    private String text;
    private String sender;

    public MessagePacket(String sender, String text) {
        super(ServerPacketType.MessagePacket.getValue());

        setText(text);
        setSender(sender);
        buildMessageData();
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void buildMessageData() {
        writeS(sender);
        writeS(text);
        buildPacket();
    }
}
