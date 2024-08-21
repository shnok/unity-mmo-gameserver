package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.StatusUpdatePacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.dto.external.serverpackets.item.InventoryUpdatePacket;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.GameClientThread;
import javolution.util.FastList;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Getter
public class RequestDestroyItemPacket extends ClientPacket {
    private final int itemObjectId;
    private final int count;

    public RequestDestroyItemPacket(GameClientThread client, byte[] data) {
        super(client, data);
        itemObjectId = readI();
        count = readI();

        handlePacket();
    }

    @Override
    public void handlePacket() {

        PlayerInstance player = client.getCurrentPlayer();
        if (player == null) {
            return;
        }

        int count = getCount();
        int objectId = getItemObjectId();

        if (count <= 0) {
            if (count < 0) {
                log.info("[MALICIOUS][ITEM][{}] Count < 0", player.getId());
            }
            return;
        }

//        if (activeChar.getPrivateStoreType() != 0)
//        {
//            activeChar.sendPacket(new SystemMessagePacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE));
//            return;
//        }

        ItemInstance itemToRemove = player.getInventory().getItemByObjectId(objectId);
        // if we cant find requested item, its actualy a cheat!
        if (itemToRemove == null) {
            return;
        }

        // Cannot discard item that the skill is consumming
        if (player.isCasting()) {
            player.sendPacket(new SystemMessagePacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM));
        }

        if (!itemToRemove.getItem().isDestroyable()) {
            player.sendPacket(new SystemMessagePacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM));
            return;
        }

        if (!itemToRemove.isStackable() && (count > 1)) {
            log.info("[MALICIOUS][ITEM][{}] Count > 1 but item is not stackable! < 0", player.getId());
            return;
        }

        if (count > itemToRemove.getCount()) {
            count = itemToRemove.getCount();
        }

        // Unequip the item if needed
        if (itemToRemove.isEquipped()) {
            if (player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAlikeDead()) {
                player.sendMessage("Your status does not allow you to do that.");
                return;
            }

            if (player.isAttacking() || player.isCasting()) {
                return;
            }

            player.getInventory().unEquipItemInSlotAndRecord(ItemSlot.getSlot(itemToRemove.getSlot()));

            // show the update in the inventory
            List<ItemInstance> items = player.getInventory().getUpdatedItems();
            InventoryUpdatePacket iu = new InventoryUpdatePacket(items);
            iu.writeMe();
            player.sendPacket(iu);

            player.getInventory().resetAndApplyUpdatedItems();

            //TODO: Update soulshots

            player.abortAttack();
            player.broadcastUserInfo();
        }

        // Destroy the item from inventory
        ItemInstance removedItem = player.getInventory().destroyItem(objectId, count, player);
        if (removedItem == null) {
            return;
        }

        // Share the item update
        InventoryUpdatePacket iu = new InventoryUpdatePacket(new FastList<>());
        if (removedItem.getCount() == 0) {
            iu.addRemovedItem(removedItem);
        } else {
            iu.addModifiedItem(removedItem);
        }
        iu.writeMe();
        player.sendPacket(iu);

        // Share new weight
        StatusUpdatePacket su = new StatusUpdatePacket(player);
        su.addAttribute(StatusUpdatePacket.CUR_LOAD, player.getCurrentLoad());
        su.build();
        player.sendPacket(su);

        // Remove object from pool
        WorldManagerService.getInstance().removeObject(removedItem);
    }
}