package com.shnok.javaserver.model.shortcut;

import com.shnok.javaserver.enums.ShortcutType;
import lombok.Getter;

@Getter
public class Shortcut {
    /**
     * Slot from 0 to 11.
     */
    private final int slot;
    /**
     * Page from 0 to 9.
     */
    private final int page;
    /**
     * Type: item, skill, action, macro, recipe, bookmark.
     */
    private final ShortcutType type;
    /**
     * Shortcut ID.
     */
    private final int id;
    /**
     * Shortcut level (skills).
     */
    private final int level;
    /**
     * Character type: 1 player, 2 summon.
     */
    private final int characterType;

    public Shortcut(int slot, int page, ShortcutType type, int id, int level, int characterType) {
        this.slot = slot;
        this.page = page;
        this.type = type;
        this.id = id;
        this.level = level;
        this.characterType = characterType;
    }
}
