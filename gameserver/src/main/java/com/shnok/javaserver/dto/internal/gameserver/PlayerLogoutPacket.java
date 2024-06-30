package com.shnok.javaserver.dto.internal.gameserver;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.internal.GameServerPacketType;

public class PlayerLogoutPacket extends SendablePacket {
    public PlayerLogoutPacket(String player) {
        super(GameServerPacketType.PlayerLogout.getValue());

        writeS(player);

        buildPacket();
    }
}
