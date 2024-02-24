package com.shnok.javaserver.service.factory;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.db.entity.DBCharTemplate;
import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.db.entity.DBPlayerItem;
import com.shnok.javaserver.db.repository.CharTemplateRepository;
import com.shnok.javaserver.db.repository.CharacterRepository;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.template.PlayerTemplate;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.service.db.ItemTableService;
import com.shnok.javaserver.service.db.PlayerItemTableService;
import com.shnok.javaserver.service.db.PlayerTableService;
import com.shnok.javaserver.thread.ai.PlayerAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class PlayerFactoryService {
    private static PlayerFactoryService instance;
    public static PlayerFactoryService getInstance() {
        if (instance == null) {
            instance = new PlayerFactoryService();
        }
        return instance;
    }

    public PlayerInstance getPlayerInstanceById(int id) {
        // TODO: get actual player id
        DBCharacter character = PlayerTableService.getInstance().getRandomCharacter();

        // Load equipped items
        List<DBPlayerItem> equipped = PlayerItemTableService.getInstance().getEquippedItemsForPlayer(character.getId());
        List<DBItem> equippedData = ItemTableService.getInstance().getPlayerItemData(equipped);
        log.debug("Player {} has {} equipped item(s).", character.getId(), equippedData.size());

        // Load inventory
        List<DBPlayerItem> inventory = PlayerItemTableService.getInstance().getInventoryItemsForPlayer(character.getId());
        List<DBItem> inventoryData = ItemTableService.getInstance().getPlayerItemData(inventory);
        log.debug("Player {} has {} item(s) in his inventory.", character.getId(), inventoryData.size());

        //TODO add items in playerinstance inventory

        PlayerTemplate playerTemplate = new PlayerTemplate(character);
        PlayerInstance player = new PlayerInstance(character.getCharName(), playerTemplate);
        player.setId(WorldManagerService.getInstance().nextID());

        //TODO: Use the character pos or add setting for defined spawn point
        player.setPosition(VectorUtils.randomPos(Config.PLAYER_SPAWN_POINT, 1.5f));

        // AI initialization
        PlayerAI ai = new PlayerAI();
        ai.setOwner(player);
        player.setAi(ai);

        return player;
    }
}
