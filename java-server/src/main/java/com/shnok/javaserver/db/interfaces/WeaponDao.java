package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBWeapon;

import java.util.List;

public interface WeaponDao {
    public DBWeapon getWeaponById(int id);
    public List<DBWeapon> getAllWeapons();
}
