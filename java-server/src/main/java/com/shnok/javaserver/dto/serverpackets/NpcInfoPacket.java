package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.entity.NpcInstance;

public class NpcInfoPacket extends ServerPacket {
    public NpcInfoPacket(NpcInstance npc) {
        super(ServerPacketType.NpcInfo.getValue());

        writeI(npc.getId());
        writeI(npc.getNpcId());
        writeF(npc.getPosX());
        writeF(npc.getPosY());
        writeF(npc.getPosZ());
        writeI(npc.getStatus().getLevel());
        writeI(npc.getStatus().getHp());
        writeI(npc.getStatus().getMaxHp());
        buildPacket();
    }
}
