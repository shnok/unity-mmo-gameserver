package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBCharacter;

public interface CharacterDao {
    public DBCharacter getRandomCharacter();
}
