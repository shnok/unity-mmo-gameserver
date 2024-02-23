package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBZoneList;

import java.util.List;

public interface ZoneListDao {
    public List<DBZoneList> getAllZoneList();
    public DBZoneList getZoneListById(String id);
}
