package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBNpc;

public interface NpcDao {
    public DBNpc getNpcById(int id);
}
