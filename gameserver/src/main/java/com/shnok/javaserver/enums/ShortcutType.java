package com.shnok.javaserver.enums;

import lombok.Getter;

@Getter
public enum ShortcutType {
    NONE(0),
    ITEM(1),
    SKILL(2),
    ACTION(3),
    MACRO(4),
    RECIPE(5),
    BOOKMARK(6);

    private final int value;

    ShortcutType(int value) {
        this.value = value;
    }

    public static ShortcutType getById(int id) {
        for (ShortcutType type : values()) {
            if (type.value == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ClassId with id " + id);
    }
}

