package com.shnok.javaserver.model.item;

import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.enums.item.Material;

public class Item {
    protected final Enum<?> type;
    private final int itemId;
    private final String name;
    private final int weight;
    private final boolean stackable;
    private final Material materialType;
    private final int duration;
    private final boolean sellable;
    private final boolean droppable;
    private final boolean crystallizable;
    private final int crystalCount;
    private final boolean destroyable;
    private final boolean tradeable;

    protected Item(Enum<?> type, DBItem item) {
        this.type = type;
        itemId = item.getId();
        name = item.getName();
        weight = item.getWeight();
        crystallizable = item.getCrystalCount() > 0;
        stackable = false;
        materialType = item.getMaterial();
        duration = item.getDuration();
        crystalCount = item.getCrystalCount();
        sellable = item.isSellable();
        droppable = item.isDroppable();
        destroyable = item.isDestroyable();
        tradeable = item.isTradeable();
    }

    @Override
    public String toString() {
        return name;
    }
}
