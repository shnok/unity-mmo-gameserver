package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;
import lombok.Getter;

@Getter
public class RequestSendMessagePacket extends ClientPacket {
    private final String message;

    public RequestSendMessagePacket(byte[] data) {
        super(data);

        message = readS();
    }
}