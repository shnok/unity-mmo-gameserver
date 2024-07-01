package com.shnok.javaserver.model;

import com.shnok.javaserver.db.entity.DBPlayerItem;
import com.shnok.javaserver.db.repository.PlayerItemRepository;
import com.shnok.javaserver.enums.item.ItemSlot;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CharSelectInfoPackage {
    private String name;
    private int objectId = 0;
    private int exp = 0;
    private int sp = 0;
    private int clanId = 0;
    private int race = 0;
    private int classId = 0;
    private long deleteTimer = 0L;
    private long lastAccess = 0L;
    private int face = 0;
    private int hairStyle = 0;
    private int hairColor = 0;
    private int sex = 0;
    private int level = 1;
    private int maxCp = 0;
    private int currentCp = 0;
    private int maxHp = 0;
    private int currentHp = 0;
    private int maxMp = 0;
    private int currentMp = 0;
    private final int[] paperdoll;
    private int karma = 0;
    private int pkKills = 0;
    private int pvpKills = 0;
    private float x = 0;
    private float y = 0;
    private float z = 0;
    private int accessLevel = 0;

    public CharSelectInfoPackage(int objectId, String name) {
        setObjectId(objectId);
        setName(name);

        paperdoll = new int[31];

        List<DBPlayerItem> equippedItems = PlayerItemRepository.getInstance().getEquippedItemsForUser(objectId);
        equippedItems.forEach((item) -> {
            paperdoll[item.getSlot()] = item.getItemId();
        });
    }

    public int getPaperdollItemId(byte slot) {
        return paperdoll[slot];
    }
}
