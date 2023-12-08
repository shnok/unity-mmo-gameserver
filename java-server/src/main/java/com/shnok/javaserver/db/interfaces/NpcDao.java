package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.Npc;

public interface NpcDao {
    public Npc getNpcById(int id);
}
