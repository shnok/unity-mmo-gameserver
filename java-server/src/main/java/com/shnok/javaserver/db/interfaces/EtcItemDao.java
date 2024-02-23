package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBEtcItem;

import java.util.List;

public interface EtcItemDao {
    public DBEtcItem getEtcItemById(int id);
    public List<DBEtcItem> getAllEtcItems();
}
