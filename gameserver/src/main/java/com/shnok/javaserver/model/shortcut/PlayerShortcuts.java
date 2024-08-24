package com.shnok.javaserver.model.shortcut;

import com.shnok.javaserver.dto.external.serverpackets.shortcut.ShortcutInitPacket;
import com.shnok.javaserver.dto.external.serverpackets.shortcut.ShortcutRegisterPacket;
import com.shnok.javaserver.enums.ShortcutType;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.TreeMap;

@Log4j2
public class PlayerShortcuts {
    private static final int MAX_SHORTCUTS_PER_BAR = 12;
    private final PlayerInstance owner;
    private final Map<Integer, Shortcut> _shortCuts = new TreeMap<>();

    public PlayerShortcuts(PlayerInstance owner) {
        this.owner = owner;
    }

    public Shortcut[] getAllShortCuts() {
        return _shortCuts.values().toArray(new Shortcut[_shortCuts.size()]);
    }

    public Shortcut getShortCut(int slot, int page) {
        Shortcut sc = _shortCuts.get(slot + (page * MAX_SHORTCUTS_PER_BAR));
        // Verify shortcut
        if ((sc != null) && (sc.getType() == ShortcutType.ITEM)) {
            if (owner.getInventory().getItemByObjectId(sc.getId()) == null) {
                deleteShortCut(sc.getSlot(), sc.getPage());
                sc = null;
            }
        }
        return sc;
    }

    public synchronized void registerShortCut(Shortcut shortcut) {
        // Verify shortcut
        if (shortcut.getType() == ShortcutType.ITEM) {
            final ItemInstance item = owner.getInventory().getItemByObjectId(shortcut.getId());
            if (item == null) {
                return;
            }
            //shortcut.setSharedReuseGroup(item.getSharedReuseGroup());
        }
        final Shortcut oldShortCut = _shortCuts.put(shortcut.getSlot() + (shortcut.getPage() * MAX_SHORTCUTS_PER_BAR), shortcut);
        registerShortCutInDb(shortcut, oldShortCut);
    }

    private void registerShortCutInDb(Shortcut shortcut, Shortcut oldShortCut) {
        if (oldShortCut != null) {
            deleteShortCutFromDb(oldShortCut);
        }

        //TODO: Save in DB
    }

    public synchronized void deleteShortCut(int slot, int page) {
        final Shortcut old = _shortCuts.remove(slot + (page * MAX_SHORTCUTS_PER_BAR));
        if ((old == null) || (owner == null)) {
            return;
        }
        deleteShortCutFromDb(old);
        if (old.getType() == ShortcutType.ITEM) {
            ItemInstance item = owner.getInventory().getItemByObjectId(old.getId());

            //TODO: Handle soulshots
//            if ((item != null) && (item.getItem().getType() == EtcItemType.SHOT)) {
//                if (_owner.removeAutoSoulShot(item.getId())) {
//                    _owner.sendPacket(new ExAutoSoulShot(item.getId(), 0));
//                }
//            }
        }

        owner.sendPacket(new ShortcutInitPacket(owner));
        //TODO: Handle soulshots
//        for (int shotId : _owner.getAutoSoulShot()) {
//            _owner.sendPacket(new ExAutoSoulShot(shotId, 1));
//        }
    }

    public synchronized void deleteShortCutByObjectId(int objectId) {
        for (Shortcut shortcut : _shortCuts.values()) {
            if ((shortcut.getType() == ShortcutType.ITEM) && (shortcut.getId() == objectId)) {
                deleteShortCut(shortcut.getSlot(), shortcut.getPage());
                break;
            }
        }
    }

    private void deleteShortCutFromDb(Shortcut shortcut) {
        //TODO: Save in DB
    }

    public boolean load() {
        _shortCuts.clear();

        // TODO: Load from DB

        // Verify shortcuts
        for (Shortcut sc : getAllShortCuts()) {
            if (sc.getType() == ShortcutType.ITEM) {
                ItemInstance item = owner.getInventory().getItemByObjectId(sc.getId());
                if (item == null) {
                    deleteShortCut(sc.getSlot(), sc.getPage());
                }
            }
        }

        return true;
    }

    /**
     * Updates the shortcut bars with the new skill.
     * @param skillId the skill Id to search and update.
     * @param skillLevel the skill level to update.
     */
    public synchronized void updateShortCuts(int skillId, int skillLevel) {
        // Update all the shortcuts for this skill
        for (Shortcut sc : _shortCuts.values()) {
            if ((sc.getId() == skillId) && (sc.getType() == ShortcutType.SKILL)) {
                final Shortcut shortcut = new Shortcut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLevel, 1);
                owner.sendPacket(new ShortcutRegisterPacket(shortcut));
                owner.registerShortCut(shortcut);
            }
        }
    }
}