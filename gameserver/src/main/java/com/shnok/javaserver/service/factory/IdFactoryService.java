package com.shnok.javaserver.service.factory;

import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.entity.DBPlayerItem;
import com.shnok.javaserver.db.repository.CharacterRepository;
import com.shnok.javaserver.db.repository.PlayerItemRepository;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public abstract class IdFactoryService {
    protected boolean initialized;

    private static IdFactoryService instance;
    public static IdFactoryService getInstance() {
        if (instance == null) {
            instance = new BitSetIDFactory();
        }
        return instance;
    }

    public int[] extractUsedObjectIDs() {
        List<DBPlayerItem> playerItems = PlayerItemRepository.getInstance().getAllPlayerItems();
        List<DBCharacter> characters = CharacterRepository.getInstance().getAllCharacters();

        int[] usedObjectIds = new int[playerItems.size() + characters.size()];

        AtomicInteger id = new AtomicInteger();
        playerItems.forEach((item) -> {
            usedObjectIds[id.getAndIncrement()] = item.getObjectId();
        });
        characters.forEach((character) -> {
            usedObjectIds[id.getAndIncrement()] = character.getId();
        });

        log.debug("[IDFACTORY] Initialized with {} object id(s) already used.", usedObjectIds.length);
        return usedObjectIds;
    }

    public abstract int getNextId();

    /**
     * return a used Object ID back to the pool
     * @param id
     */
    public abstract void releaseId(int id);

    public abstract int size();
}
