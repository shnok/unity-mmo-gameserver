package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.repository.CharacterRepository;

public class PlayerTableService {
    private final CharacterRepository characterRepository;
    private static PlayerTableService instance;
    public static PlayerTableService getInstance() {
        if (instance == null) {
            instance = new PlayerTableService();
        }
        return instance;
    }

    public PlayerTableService() {
        characterRepository = new CharacterRepository();
    }

    public DBCharacter getRandomCharacter() {
        return characterRepository.getRandomCharacter();
    }
}
