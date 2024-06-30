package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBCharacter;

import java.util.List;

public interface CharacterDao {
    public DBCharacter getRandomCharacter();
    public DBCharacter getCharacterById(int id);
    public List<DBCharacter> getCharactersForAccount(String account);
    public void saveOrUpdateCharacter(DBCharacter character);

    int saveCharacter(DBCharacter character);
}
