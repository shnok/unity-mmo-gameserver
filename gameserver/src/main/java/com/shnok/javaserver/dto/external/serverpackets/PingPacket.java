package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class PingPacket extends SendablePacket {
    public PingPacket() {
        super(ServerPacketType.Ping.getValue());
        setData(new byte[]{ServerPacketType.Ping.getValue(), 0x02});
        buildPacket();
    }
}
