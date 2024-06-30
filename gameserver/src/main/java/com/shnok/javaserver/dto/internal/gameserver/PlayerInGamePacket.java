package com.shnok.javaserver.dto.internal.gameserver;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.internal.GameServerPacketType;

import java.util.List;

public class PlayerInGamePacket extends SendablePacket {
    public PlayerInGamePacket(String player) {
        super(GameServerPacketType.PlayerInGame.getValue());

        writeI(1);
        writeS(player);

        buildPacket();
    }

    public PlayerInGamePacket(List<String> players) {
        super(GameServerPacketType.PlayerInGame.getValue());

        writeI(players.size());
        players.forEach(this::writeS);

        buildPacket();
    }
}
