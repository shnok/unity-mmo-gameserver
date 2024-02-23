package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBCharTemplate;

public interface CharTemplateDao {
    public DBCharTemplate getTemplateByClassId(int id);
}
