package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
@Getter
public class ProtocolVersionPacket extends ReceivablePacket {
    private final int version;
    public ProtocolVersionPacket(byte[] data) {
        super(data);
        version = readI();
    }
}
