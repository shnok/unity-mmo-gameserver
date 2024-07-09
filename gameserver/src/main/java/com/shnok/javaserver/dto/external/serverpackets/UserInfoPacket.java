package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

import java.util.Objects;

import static com.shnok.javaserver.dto.external.serverpackets.PlayerInfoPacket.PAPERDOLL_ORDER;

public class UserInfoPacket extends SendablePacket {
    public UserInfoPacket(PlayerInstance player) {
        super(ServerPacketType.UserInfo.getValue());

        writeI(player.getId());
        writeS(player.getName());
        writeB(player.getTemplate().getClassId().getId());
        writeB(player.getTemplate().getClassId().isMage() ? (byte) 1 : (byte) 0);
        writeF(player.getPosition().getHeading());
        writeF(player.getPosX());
        writeF(player.getPosY());
        writeF(player.getPosZ());
        // Status
        writeI(player.getLevel());
        writeI((int) player.getStatus().getCurrentHp());
        writeI(player.getStat().getMaxHp());
        // Stats
        writeI((int) player.getStat().getMoveSpeed());
        writeI(player.getTemplate().getBasePAtkSpd());
        writeI(player.getTemplate().getBaseMAtkSpd());
        // Appearance
        writeF(player.getTemplate().getCollisionHeight());
        writeF(player.getTemplate().getCollisionRadius());
        writeB(player.getTemplate().getRace().getValue());
        writeB(player.getAppearance().isSex() ? (byte) 1 : (byte) 0);
        writeB(player.getAppearance().getFace());
        writeB(player.getAppearance().getHairStyle());
        writeB(player.getAppearance().getHairColor());

        // Gear
        for (byte slot : PAPERDOLL_ORDER) {
            writeI(player.getInventory().getEquippedItemId(Objects.requireNonNull(ItemSlot.getSlot(slot))));
        }

        buildPacket();
    }
}
