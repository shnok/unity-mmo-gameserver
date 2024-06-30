package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBLevelUpGain;

public interface ClassLevelDao {
    public DBLevelUpGain getCharacterByClassId(int id);
}
