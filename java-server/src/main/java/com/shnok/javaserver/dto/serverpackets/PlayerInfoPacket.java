package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.entity.PlayerInstance;

public class PlayerInfoPacket extends ServerPacket {
    public PlayerInfoPacket(PlayerInstance player) {
        super(ServerPacketType.PlayerInfo.getValue());

        writeI(player.getId());
        writeS(player.getName());
        writeF(player.getPosX());
        writeF(player.getPosY());
        writeF(player.getPosZ());
        writeI(player.getStatus().getMoveSpeed());
        writeI(player.getTemplate().getBasePAtkSpd());
        writeI(player.getTemplate().getBaseMAtkSpd());
        writeF(player.getTemplate().getBaseAtkRange());
        writeI(player.getStatus().getLevel());
        writeI(player.getStatus().getHp());
        writeI(player.getStatus().getMaxHp());
        writeI(player.getStatus().getMp());
        writeI(player.getStatus().getMaxMp());
        writeI(player.getStatus().getCp());
        writeI(player.getStatus().getMaxCp());
        buildPacket();
    }
}
