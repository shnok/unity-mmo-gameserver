package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.dto.external.serverpackets.item.InventoryUpdatePacket;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Getter
public class UseItemPacket extends ClientPacket {
    private final int itemObjectId;

    public UseItemPacket(GameClientThread client, byte[] data) {
        super(client, data);
        itemObjectId = readI();

        handlePacket();
    }

    @Override
    public void handlePacket() {

        PlayerInstance player = client.getCurrentPlayer();
        if (player == null) {
            return;
        }

        ItemInstance item = player.getInventory().getItemByObjectId(getItemObjectId());
        if (item == null) {
            return;
        }

        int itemId = item.getItemId();

        if (player.isDead()) {
            SystemMessagePacket sm = new SystemMessagePacket(SystemMessageId.S1_CANNOT_BE_USED);
            sm.addItemName(itemId);
            sm.writeMe();

            player.sendPacket(sm);
            return;
        }

        log.debug("[ITEM][{}] Use item {}", player.getId(), itemId);

        if (item.isEquipable()) {
            // No unequipping/equipping while the player is in special conditions
            if (player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAlikeDead()) {
                player.sendMessage("Your status does not allow you to do that.");
                return;
            }

            ItemSlot bodyPart = item.getItem().getBodyPart();
            // Prevent player to remove the weapon on special conditions
            if ((player.isAttacking() || player.isCasting()) && ((bodyPart == ItemSlot.lrhand) ||
                    (bodyPart == ItemSlot.lhand) || (bodyPart == ItemSlot.rhand))) {
                return;
            }

            // Equip or unEquip
            boolean isEquipped = item.isEquipped();
            SystemMessagePacket sm = null;

            //TODO: Update soulshots
//            ItemInstance old = player.getInventory().getEquippedItem(ItemSlot.lrhand);
//            if (old == null) {
//                old = player.getInventory().getEquippedItem(ItemSlot.rhand);
//                activeChar.checkSSMatch(item, old);
//            }

            if (isEquipped) {
                if (item.getEnchantLevel() > 0) {
                    sm = new SystemMessagePacket(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
                    sm.addInt(item.getEnchantLevel());
                    sm.addItemName(itemId);
                } else {
                    sm = new SystemMessagePacket(SystemMessageId.S1_DISARMED);
                    sm.addItemName(itemId);
                }
                sm.writeMe();
                player.sendPacket(sm);

                player.getInventory().unEquipItemInSlotAndRecord(ItemSlot.getSlot((byte) item.getSlot()));
            } else {
                if (item.getEnchantLevel() > 0) {
                    sm = new SystemMessagePacket(SystemMessageId.S1_S2_EQUIPPED);
                    sm.addInt(item.getEnchantLevel());
                    sm.addItemName(itemId);
                } else {
                    sm = new SystemMessagePacket(SystemMessageId.S1_EQUIPPED);
                    sm.addItemName(itemId);
                }
                sm.writeMe();
                player.sendPacket(sm);
                player.getInventory().equipItemAndRecord(item);
            }

            //TODO: Update grade penalty
            //activeChar.refreshExpertisePenalty();

            List<ItemInstance> items = player.getInventory().getUpdatedItems();

            InventoryUpdatePacket iu = new InventoryUpdatePacket(items);
            iu.writeMe();
            player.sendPacket(iu);

            player.getInventory().resetAndApplyUpdatedItems();

            player.abortAttack();
            player.broadcastUserInfo();
        } else {
            //TODO: Handle use other items
            log.warn("No item handler registered for item ID {}.", item.getItemId());
        }
    }
}
