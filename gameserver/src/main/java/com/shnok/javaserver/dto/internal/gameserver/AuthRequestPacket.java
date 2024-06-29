package com.shnok.javaserver.dto.internal.gameserver;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.internal.GameServerPacketType;

import java.util.List;

public class AuthRequestPacket extends SendablePacket {
    public AuthRequestPacket(int id, boolean acceptAlternate, byte[] hexid, int port, int maxplayer,
                             List<String> subnets, List<String> hosts) {
        super(GameServerPacketType.AuthRequest.getValue());

        writeB((byte) id);
        writeB(acceptAlternate ? (byte) 0x01 : (byte) 0x00);
        writeI(port);
        writeI(maxplayer);
        writeI(hexid.length);
        writeB(hexid);
        writeI(subnets.size());

        for (int i = 0; i < subnets.size(); i++) {
            writeS(subnets.get(i));
            writeS(hosts.get(i));
        }

        buildPacket();
    }
}
