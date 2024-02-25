package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

public class UserInfoPacket extends ServerPacket {
    public UserInfoPacket(PlayerInstance player) {
        super(ServerPacketType.UserInfo.getValue());

        writeI(player.getId());
        writeS(player.getName());
        writeF(player.getPosition().getHeading());
        writeF(player.getPosX());
        writeF(player.getPosY());
        writeF(player.getPosZ());
        // Status
        writeI(player.getStatus().getLevel());
        writeI(player.getStatus().getHp());
        writeI(player.getStatus().getMaxHp());
        // Stats
        writeI(player.getStatus().getMoveSpeed());
        writeI(player.getTemplate().getBasePAtkSpd());
        writeI(player.getTemplate().getBaseMAtkSpd());
        // Appearance
        writeF(player.getTemplate().getCollisionHeight());
        writeF(player.getTemplate().getCollisionRadius());
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
