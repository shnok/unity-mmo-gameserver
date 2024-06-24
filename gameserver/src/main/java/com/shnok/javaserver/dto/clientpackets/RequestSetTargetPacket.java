package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;
import lombok.Getter;

@Getter
public class RequestSetTargetPacket extends ClientPacket {
    private final int targetId;

    public RequestSetTargetPacket(byte[] data) {
        super(data);

        targetId = readI();
    }
}
