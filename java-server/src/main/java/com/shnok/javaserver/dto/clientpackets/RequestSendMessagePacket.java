package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;

public class RequestSendMessagePacket extends ClientPacket {

    private final String _message;

    public RequestSendMessagePacket(byte[] data) {
        super(data);

        _message = readS();
    }

    public String getMessage() {
        return _message;
    }
}