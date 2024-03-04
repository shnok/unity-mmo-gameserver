package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.*;
import com.shnok.javaserver.db.repository.ArmorRepository;
import com.shnok.javaserver.db.repository.CharTemplateRepository;
import com.shnok.javaserver.db.repository.EtcItemRepository;
import com.shnok.javaserver.db.repository.WeaponRepository;
import javolution.util.FastList;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;

@Log4j2
public class ItemTableService {
    private final ArmorRepository armorRepository;
    private final WeaponRepository weaponRepository;
    private final EtcItemRepository etcItemRepository;
    private final Map<Integer, DBArmor> armorData;
    private final Map<Integer, DBWeapon> weaponData;
    private final Map<Integer, DBEtcItem> etcItemsData;
    private final Map<Integer, DBItem> itemData;

    private static ItemTableService instance;
    public static ItemTableService getInstance() {
        if (instance == null) {
            instance = new ItemTableService();
        }
        return instance;
    }

    public ItemTableService() {
        armorRepository = new ArmorRepository();
        weaponRepository = new WeaponRepository();
        etcItemRepository = new EtcItemRepository();
        armorData = new FastMap<>();
        weaponData = new FastMap<>();
        etcItemsData = new FastMap<>();
        itemData = new FastMap<>();

        LoadAllItems();
    }

    private void LoadAllItems() {
        log.info("Loading all items from DB...");

        List<DBArmor> allArmors = armorRepository.getAllArmors();
        allArmors.forEach((armor) -> {
            armorData.put(armor.getId(), armor);
            itemData.put(armor.getId(), armor);
            log.debug(armor.toString());
        });
        log.info("Loaded {} armor(s) from DB.", armorData.size());

        List<DBWeapon> allWeapons = weaponRepository.getAllWeapons();
        allWeapons.forEach((weapon) -> {
            weaponData.put(weapon.getId(), weapon);
            itemData.put(weapon.getId(), weapon);
            log.debug(weapon.toString());
        });
        log.info("Loaded {} weapon(s) from DB.", weaponData.size());

        List<DBEtcItem> allEtcItems = etcItemRepository.getAllEtcItems();
        allEtcItems.forEach((etcItem) -> {
            etcItemsData.put(etcItem.getId(), etcItem);
            itemData.put(etcItem.getId(), etcItem);
            log.debug(etcItem.toString());
        });
        log.info("Loaded {} etcItem(s) from DB.", etcItemsData.size());

        log.info("Loaded {} item(s) from DB.", itemData.size());
    }

    public DBItem getItemById(int id) {
        return itemData.get(id);
    }

    public DBWeapon getWeaponById(int id) {
        return weaponData.get(id);
    }

    public DBArmor getArmorById(int id) {
        return armorData.get(id);
    }

    public DBEtcItem getEtcItemById(int id) {
        return etcItemsData.get(id);
    }

    public List<DBItem> getPlayerItemData(List<DBPlayerItem> playerItems) {
        List<DBItem> playerItemData = new FastList<>();
        playerItems.forEach((pi) -> {
            playerItemData.add(getItemById(pi.getItemId()));
        });

        return playerItemData;
    }
}
