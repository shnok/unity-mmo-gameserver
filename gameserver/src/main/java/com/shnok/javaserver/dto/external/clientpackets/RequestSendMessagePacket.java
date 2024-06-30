package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestSendMessagePacket extends ReceivablePacket {
    private final String message;

    public RequestSendMessagePacket(byte[] data) {
        super(data);

        message = readS();
    }
}