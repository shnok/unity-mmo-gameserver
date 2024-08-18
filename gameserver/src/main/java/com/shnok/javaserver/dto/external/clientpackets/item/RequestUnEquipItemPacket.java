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
import java.util.Objects;

@Log4j2
@Getter
public class RequestUnEquipItemPacket extends ClientPacket {
    private final int slot;

    public RequestUnEquipItemPacket(GameClientThread client, byte[] data) {
        super(client, data);
        slot = readI();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        PlayerInstance player = client.getCurrentPlayer();
        if (player == null) {
            return;
        }

        ItemSlot slot = Objects.requireNonNull(ItemSlot.getSlot((byte) getSlot()));

        log.debug("[ITEM][{}] UnEquip item from slot {}", player.getId(), slot);

        // Prevent player from unequipping items in special conditions
        if (player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAlikeDead()) {
            player.sendMessage("Your status does not allow you to do that.");
            return;
        }

        if (player.isAttacking() || player.isCasting()) {
            return;
        }

        player.getInventory().unEquipItemInSlotAndRecord(slot);

        // show the update in the inventory
        List<ItemInstance> items = player.getInventory().getUpdatedItems();
        InventoryUpdatePacket iu = new InventoryUpdatePacket(items);
        iu.writeMe();
        player.sendPacket(iu);

        player.getInventory().resetAndApplyUpdatedItems();

        //TODO: Update soulshots

        player.abortAttack();
        player.broadcastUserInfo();

        // this can be 0 if the user pressed the right mousebutton twice very fast
        if (!items.isEmpty()) {
            SystemMessagePacket sm;
            if (items.get(0).getEnchantLevel() > 0) {
                sm = new SystemMessagePacket(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
                sm.addInt(items.get(0).getEnchantLevel());
                sm.addItemName(items.get(0).getItemId());
            } else {
                sm = new SystemMessagePacket(SystemMessageId.S1_DISARMED);
                sm.addItemName(items.get(0).getItemId());
            }
            sm.writeMe();
            player.sendPacket(sm);
        }
    }
}
