package com.shnok.serverpackets;

import com.shnok.model.PlayerInstance;

public class PlayerInfo extends ServerPacket {
    public PlayerInfo(PlayerInstance player) {
        super((byte)0x04);

        writeI(player.getId());
        writeS(player.getName());
        writeI(player.getPosX());
        writeI(player.getPosY());
        writeI(player.getPosZ());
        writeI(player.getStatus().getLevel());
        writeI(player.getStatus().getCurrentHp());
        writeI(player.getStatus().getMaxHp());
        writeI(player.getStatus().getStamina());
        writeI(player.getStatus().getMaxStamina());
        buildPacket();
    }
}
