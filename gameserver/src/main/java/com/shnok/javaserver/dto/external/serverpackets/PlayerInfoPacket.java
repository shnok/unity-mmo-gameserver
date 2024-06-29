package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.external.ServerPacketType;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

public class PlayerInfoPacket extends SendablePacket {
    public PlayerInfoPacket(PlayerInstance player) {
        super(ServerPacketType.PlayerInfo.getValue());

        writeI(player.getId());
        writeS(player.getName());
        writeB(player.getTemplate().getClassId().getId());
        writeB(player.getTemplate().getClassId().isMage() ? (byte) 1 : (byte) 0);
        writeF(player.getPosition().getHeading());
        writeF(player.getPosX());
        writeF(player.getPosY());
        writeF(player.getPosZ());
        // Status
        writeI(player.getStatus().getLevel());
        writeI(player.getStatus().getHp());
        writeI(player.getStatus().getMaxHp());
        writeI(player.getStatus().getMp());
        writeI(player.getStatus().getMaxMp());
        writeI(player.getStatus().getCp());
        writeI(player.getStatus().getMaxCp());
        // Stats
        // TODO: calc stats and send the correct values
        writeI(player.getStatus().getMoveSpeed());
        writeI(player.getTemplate().getBasePAtkSpd());
        writeI(player.getTemplate().getBaseMAtkSpd());
        writeF(player.getTemplate().getBaseAtkRange());
        writeB(player.getTemplate().getBaseCON());
        writeB(player.getTemplate().getBaseDEX());
        writeB(player.getTemplate().getBaseSTR());
        writeB(player.getTemplate().getBaseMEN());
        writeB(player.getTemplate().getBaseWIT());
        writeB(player.getTemplate().getBaseINT());
        // Appearance
        writeF(player.getTemplate().getCollisionHeight());
        writeF(player.getTemplate().getCollisionRadius());
        writeB(player.getTemplate().getRace().getValue());
        writeB(player.getAppearance().isSex() ? (byte) 1 : (byte) 0);
        writeB(player.getAppearance().getFace());
        writeB(player.getAppearance().getHairStyle());
        writeB(player.getAppearance().getHairColor());
        // Gear
        writeI(player.getInventory().getEquippedItemId(ItemSlot.lhand));
        writeI(player.getInventory().getEquippedItemId(ItemSlot.rhand));
        writeI(player.getInventory().getEquippedItemId(ItemSlot.chest));
        writeI(player.getInventory().getEquippedItemId(ItemSlot.legs));
        writeI(player.getInventory().getEquippedItemId(ItemSlot.gloves));
        writeI(player.getInventory().getEquippedItemId(ItemSlot.feet));

        buildPacket();
    }
}
