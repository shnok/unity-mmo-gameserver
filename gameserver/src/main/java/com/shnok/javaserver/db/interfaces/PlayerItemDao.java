package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBPlayerItem;

import java.util.List;

public interface PlayerItemDao {
    List<DBPlayerItem> getAllPlayerItems();

    List<DBPlayerItem> getAllItemsForUser(int id);
    List<DBPlayerItem> getEquippedItemsForUser(int id);
    List<DBPlayerItem> getInventoryItemsForUser(int id);
    List<DBPlayerItem> getWarehouseItemsForUser(int id);
    public int savePlayerItem(DBPlayerItem playerItem);
}
