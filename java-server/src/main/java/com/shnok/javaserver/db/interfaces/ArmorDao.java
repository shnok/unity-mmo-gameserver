package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBArmor;

import java.util.List;

public interface ArmorDao {
    public DBArmor getArmorById(int id);

    public List<DBArmor> getAllArmors();
}
