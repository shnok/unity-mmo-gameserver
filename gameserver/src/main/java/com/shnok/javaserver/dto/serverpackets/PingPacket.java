package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class PingPacket extends ServerPacket {
    public PingPacket() {
        super(ServerPacketType.Ping.getValue());
        setData(new byte[]{ServerPacketType.Ping.getValue(), 0x02});
    }
}
