package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.DBLevelUpGain;
import com.shnok.javaserver.db.repository.LvlUpGainRepository;

public class LvlUpGainTable {
    private final LvlUpGainRepository repository;
    private static LvlUpGainTable instance;
    public static LvlUpGainTable getInstance() {
        if (instance == null) {
            instance = new LvlUpGainTable();
        }
        return instance;
    }

    public LvlUpGainTable() {
        repository = new LvlUpGainRepository();
    }

    public DBLevelUpGain getCharacterByClassId(int classId) {
        return repository.getCharacterByClassId(classId);
    }
}
