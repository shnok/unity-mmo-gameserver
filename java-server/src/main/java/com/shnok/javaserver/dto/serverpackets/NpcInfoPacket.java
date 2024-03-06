package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.object.entity.NpcInstance;

public class NpcInfoPacket extends ServerPacket {
    public NpcInfoPacket(NpcInstance npc) {
        super(ServerPacketType.NpcInfo.getValue());

        writeI(npc.getId());
        writeI(npc.getTemplate().getNpcId());
        writeF(npc.getPosition().getHeading());
        writeF(npc.getPosX());
        writeF(npc.getPosY());
        writeF(npc.getPosZ());
        // Stats
        writeI(npc.getStatus().getMoveSpeed());
        writeI(npc.getTemplate().getBasePAtkSpd());
        writeI(npc.getTemplate().getBaseMAtkSpd());
        // Status
        writeI(npc.getStatus().getLevel());
        writeI(npc.getStatus().getHp());
        writeI(npc.getStatus().getMaxHp());
        buildPacket();
    }
}
