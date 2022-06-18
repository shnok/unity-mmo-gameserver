package com.shnok.serverpackets;

import com.shnok.model.entities.NpcInstance;
import com.shnok.model.entities.PlayerInstance;

public class NpcInfo extends ServerPacket {
    public NpcInfo(NpcInstance npc) {
        super((byte)0x0A);

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
