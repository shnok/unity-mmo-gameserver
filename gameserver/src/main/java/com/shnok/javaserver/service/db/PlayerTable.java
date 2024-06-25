package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.repository.CharacterRepository;

public class PlayerTable {
    private final CharacterRepository characterRepository;
    private static PlayerTable instance;
    public static PlayerTable getInstance() {
        if (instance == null) {
            instance = new PlayerTable();
        }
        return instance;
    }

    public PlayerTable() {
        characterRepository = new CharacterRepository();
    }

    public DBCharacter getRandomCharacter() {
        return characterRepository.getRandomCharacter();
    }

    public DBCharacter getCharacterById(int id) {
        return characterRepository.getCharacterById(id);
    }
}
