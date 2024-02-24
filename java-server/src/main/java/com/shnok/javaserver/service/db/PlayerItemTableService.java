package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.DBPlayerItem;
import com.shnok.javaserver.db.repository.PlayerItemRepository;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class PlayerItemTableService {
    private final PlayerItemRepository playerItemRepository;
    private static PlayerItemTableService instance;
    public static PlayerItemTableService getInstance() {
        if (instance == null) {
            instance = new PlayerItemTableService();
        }
        return instance;
    }

    public PlayerItemTableService() {
        playerItemRepository = new PlayerItemRepository();
    }

    public List<DBPlayerItem> getEquippedItemsForPlayer(int id) {
        List<DBPlayerItem> items = playerItemRepository.getEquippedItemsForUser(id);
        log.debug("Loaded {} equipped item(s) for player {}.", items.size(), id);
        return items;
    }

    public List<DBPlayerItem> getInventoryItemsForPlayer(int id) {
        List<DBPlayerItem> items = playerItemRepository.getInventoryItemsForUser(id);
        log.debug("Loaded {} item(s) player's {} inventory.", items.size(), id);
        return items;
    }

    public List<DBPlayerItem> getWarehouseItemsForPlayer(int id) {
        List<DBPlayerItem> items = playerItemRepository.getWarehouseItemsForUser(id);
        log.debug("Loaded {} item(s) player's {} warehouse.", items.size(), id);
        return items;
    }
}
