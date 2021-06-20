package com.shnok.serverpackets;

public class MessagePacket extends ServerPacket {
    private String _text;
    private String _sender;

    public MessagePacket(String sender, String text) {
        super((byte)0x02);

        setText(text);
        setSender(sender);
        buildMessageData();
    }

    public void setSender(String sender) {
        _sender = sender;
    }

    public void setText(String text) {
        _text = text;
    }

    public void buildMessageData() {
        writeS(_sender);
        writeS(_text);
        buildPacket();
    }
}
