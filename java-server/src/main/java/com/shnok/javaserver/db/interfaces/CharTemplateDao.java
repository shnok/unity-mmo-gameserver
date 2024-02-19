package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.CharTemplate;

public interface CharTemplateDao {
    public CharTemplate getTemplateByClassId(int id);
}
