package com.shnok.javaserver.model.item.listeners;

import com.shnok.javaserver.model.object.ItemInstance;

public interface GearListener {
    public void notifyEquipped(int slot, ItemInstance inst);

    public void notifyUnequipped(int slot, ItemInstance inst);
}
