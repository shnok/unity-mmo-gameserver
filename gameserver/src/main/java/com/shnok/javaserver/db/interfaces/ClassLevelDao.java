package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBLevelUpGain;

import java.util.List;

public interface ClassLevelDao {
    public DBLevelUpGain getLevelUpGainByClassId(int id);

    List<DBLevelUpGain> getAllLevelUpGainData();
}
