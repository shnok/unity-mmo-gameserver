package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;

public class RequestSendMessage extends ClientPacket {

    private final String _message;

    public RequestSendMessage(byte[] data) {
        super(data);

        _message = readS();
    }

    public String getMessage() {
        return _message;
    }
}