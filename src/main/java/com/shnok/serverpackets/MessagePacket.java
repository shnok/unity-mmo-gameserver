package com.shnok.serverpackets;

import java.nio.charset.StandardCharsets;

public class MessagePacket extends ServerPacket {
    private String _text;
    private String _sender;

    public MessagePacket(String sender, String text) {
        super((byte)0x01);

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
        byte[] senderBytes = _sender.getBytes();
        byte[] textBytes = _text.getBytes();

        byte[] result = new byte[senderBytes.length + textBytes.length + 1];

        result[0] = (byte)senderBytes.length;

        System.arraycopy(senderBytes, 0, result, 1, senderBytes.length);
        System.arraycopy(textBytes, 0, result, senderBytes.length + 1, textBytes.length);

        buildPacket(result);
    }
}
