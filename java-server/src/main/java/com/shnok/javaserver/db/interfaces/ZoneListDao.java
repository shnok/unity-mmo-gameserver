package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.ZoneList;

import java.util.List;

public interface ZoneListDao {
    public List<ZoneList> getAllZoneList();
    public ZoneList getZoneListById(String id);
}
