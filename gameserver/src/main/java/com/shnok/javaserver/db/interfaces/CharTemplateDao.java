package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBCharTemplate;

import java.util.List;

public interface CharTemplateDao {
    List<DBCharTemplate> getAllCharTemplates();

    public DBCharTemplate getTemplateByClassId(int id);
}
