package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestCharacterAnimationPacket extends ReceivablePacket {
    private final byte animId;
    private final float value;

    public RequestCharacterAnimationPacket(byte[] data) {
        super(data);
        animId = readB();
        value = readF();
    }
}
