package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBPlayerItem;

import java.util.List;

public interface ItemDao {
    public List<DBPlayerItem> getEquippedItemsForUser(int ownerId);
}
