package com.shnok.javaserver.model.item.listeners;

import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.Entity;
import lombok.Getter;

@Getter
public class StatsListener implements GearListener {
    private final Entity owner;

    public StatsListener(Entity owner) {
        this.owner = owner;
    }

    @Override
    public void notifyUnequipped(int slot, ItemInstance item) {
        getOwner().removeStatsOwner(item);
    }

    @Override
    public void notifyEquipped(int slot, ItemInstance item) {
        getOwner().addStatFuncs(item.getStatFuncs());
    }
}
