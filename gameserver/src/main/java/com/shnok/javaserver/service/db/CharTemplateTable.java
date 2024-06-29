package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.DBCharTemplate;
import com.shnok.javaserver.db.repository.CharTemplateRepository;

public class CharTemplateTable {
    private final CharTemplateRepository charTemplateRepository;
    private static CharTemplateTable instance;
    public static CharTemplateTable getInstance() {
        if (instance == null) {
            instance = new CharTemplateTable();
        }
        return instance;
    }

    public CharTemplateTable() {
        charTemplateRepository = new CharTemplateRepository();
    }

    public DBCharTemplate getTemplateByClassId(int classId) {
        return charTemplateRepository.getTemplateByClassId(classId);
    }
}
