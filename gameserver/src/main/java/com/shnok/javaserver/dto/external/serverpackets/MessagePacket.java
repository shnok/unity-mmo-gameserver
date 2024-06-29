package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.external.ServerPacketType;

public class MessagePacket extends SendablePacket {
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
