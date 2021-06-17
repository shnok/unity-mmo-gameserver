package com.shnok.serverpackets;

public class MessagePacket extends ServerPacket {
    private String _text;

    public MessagePacket(String text) {
        super((byte)0x01);
        setText(text);
    }

    public void setText(String text) {
        _text = text;
        buildPacket(_text.getBytes());
    }

    public String getText() {
        return _text;
    }
}
