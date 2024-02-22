package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.Item;

import java.util.List;

public interface ItemDao {
    public List<Item> getEquippedItemsForUser(int ownerId);
}
