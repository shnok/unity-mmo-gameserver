package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.model.entities.NpcInstance;

public class NpcInfo extends ServerPacket {
    public NpcInfo(NpcInstance npc) {
        super((byte) 0x0A);

        writeI(npc.getId());
        writeI(npc.getNpcId());
        writeF(npc.getPosX());
        writeF(npc.getPosY());
        writeF(npc.getPosZ());
        writeI(npc.getStatus().getLevel());
        writeI(npc.getStatus().getCurrentHp());
        writeI(npc.getStatus().getMaxHp());
        buildPacket();
    }
}
