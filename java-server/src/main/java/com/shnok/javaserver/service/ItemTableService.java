package com.shnok.javaserver.service;

import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.entity.DBEtcItem;
import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.db.repository.ArmorRepository;
import com.shnok.javaserver.db.repository.CharTemplateRepository;
import com.shnok.javaserver.db.repository.EtcItemRepository;
import com.shnok.javaserver.db.repository.WeaponRepository;
import com.shnok.javaserver.model.item.Item;
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
    private final Map<Integer, DBItem> allItems;

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
        allItems = new FastMap<>();

        LoadAllItems();
    }

    private void LoadAllItems() {
        log.info("Loading all items from DB...");

        CharTemplateRepository charTemplateRepository = new CharTemplateRepository();
        charTemplateRepository.getTemplateByClassId(1);

        List<DBArmor> allArmors = armorRepository.getAllArmors();
        allArmors.forEach((armor) -> {
            armorData.put(armor.getId(), armor);
            log.debug(armor.toString());
        });
        log.info("Loaded {} armor(s) from DB", armorData.size());

        List<DBWeapon> allWeapons = weaponRepository.getAllWeapons();
        allWeapons.forEach((weapon) -> {
            weaponData.put(weapon.getId(), weapon);
            log.debug(weapon.toString());
        });
        log.info("Loaded {} weapon(s) from DB", weaponData.size());

        List<DBEtcItem> allEtcItems = etcItemRepository.getAllEtcItems();
        allEtcItems.forEach((etcItem) -> {
            etcItemsData.put(etcItem.getId(), etcItem);
            log.debug(etcItem.toString());
        });
        log.info("Loaded {} etcItem(s) from DB", etcItemsData.size());
    }
}
