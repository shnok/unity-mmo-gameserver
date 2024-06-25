package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestSetTargetPacket extends ReceivablePacket {
    private final int targetId;

    public RequestSetTargetPacket(byte[] data) {
        super(data);

        targetId = readI();
    }
}
