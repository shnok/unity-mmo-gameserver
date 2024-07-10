package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

import java.util.Objects;

public class PlayerInfoPacket extends SendablePacket {

    public static final byte[] PAPERDOLL_ORDER = new byte[] {
            ItemSlot.lhand.getValue(),
            ItemSlot.rhand.getValue(),
            ItemSlot.chest.getValue(),
            ItemSlot.legs.getValue(),
            ItemSlot.gloves.getValue(),
            ItemSlot.feet.getValue()
    };

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
        writeI(player.getLevel());
        writeI((int) player.getStatus().getCurrentHp());
        writeI(player.getStat().getMaxHp());
        writeI((int) player.getStatus().getCurrentMp());
        writeI(player.getStat().getMaxMp());
        writeI((int) player.getStatus().getCurrentCp());
        writeI(player.getStat().getMaxCp());
        // Stats
        // TODO: calc stats and send the correct values
        writeI((int) player.getStat().getMoveSpeed());
        writeI(player.getTemplate().getBasePAtkSpd());
        writeI(player.getTemplate().getBaseMAtkSpd());
        writeF(player.getTemplate().getBaseAtkRange());
        writeB((byte) player.getTemplate().getBaseCON());
        writeB((byte) player.getTemplate().getBaseDEX());
        writeB((byte) player.getTemplate().getBaseSTR());
        writeB((byte) player.getTemplate().getBaseMEN());
        writeB((byte) player.getTemplate().getBaseWIT());
        writeB((byte) player.getTemplate().getBaseINT());
        // Appearance
        writeF(player.getTemplate().getCollisionHeight());
        writeF(player.getTemplate().getCollisionRadius()); //TODO remove (get from system grp files)
        writeB(player.getTemplate().getRace().getValue());
        writeB(player.getAppearance().isSex() ? (byte) 1 : (byte) 0);
        writeB(player.getAppearance().getFace());
        writeB(player.getAppearance().getHairStyle());
        writeB(player.getAppearance().getHairColor());

        // Gear
        for (byte slot : PAPERDOLL_ORDER) {
            int itemId = player.getInventory().getEquippedItemId(Objects.requireNonNull(ItemSlot.getSlot(slot)));
            writeI(itemId);
        }

        buildPacket();
    }
}
