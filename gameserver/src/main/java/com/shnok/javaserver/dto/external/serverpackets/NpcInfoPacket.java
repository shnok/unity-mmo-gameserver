package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.object.entity.NpcInstance;

public class NpcInfoPacket extends SendablePacket {
    public NpcInfoPacket(NpcInstance npc) {
        super(ServerPacketType.NpcInfo.getValue());

        writeI(npc.getId());
        writeI(npc.getTemplate().getNpcId());
        writeF(npc.getPosition().getHeading());
        writeF(npc.getPosX());
        writeF(npc.getPosY());
        writeF(npc.getPosZ());
        // Stats
        writeI((int) npc.getStat().getMoveSpeed());
        writeI(npc.getTemplate().getBasePAtkSpd());
        writeI(npc.getTemplate().getBaseMAtkSpd());
        // Status
        writeI(npc.getLevel());
        writeI((int) npc.getStatus().getCurrentHp());
        writeI(npc.getStat().getMaxHp());
        buildPacket();
    }
}
