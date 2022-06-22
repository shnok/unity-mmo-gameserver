package com.shnok.serverpackets;

public class PingPacket extends ServerPacket {
    public PingPacket() {
        super((byte) 0x00);
        setData(new byte[]{0x00, 0x02});
    }
}
