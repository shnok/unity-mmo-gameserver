package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.model.entities.PlayerInstance;

public class UserInfo extends ServerPacket {
    public UserInfo(PlayerInstance player) {
        super((byte) 0x04);

        writeI(player.getId());
        writeS(player.getName());
        writeF(player.getPosX());
        writeF(player.getPosY());
        writeF(player.getPosZ());
        writeI(player.getStatus().getLevel());
        writeI(player.getStatus().getCurrentHp());
        writeI(player.getStatus().getMaxHp());
        writeI(player.getStatus().getStamina());
        writeI(player.getStatus().getMaxStamina());
        buildPacket();
    }
}