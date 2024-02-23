package com.shnok.javaserver.enums.item;

public enum EtcItemType {
    ARROW(0, "Arrow"),
    MATERIAL(1, "Material"),
    PET_COLLAR(2, "PetCollar"),
    POTION(3, "Potion"),
    RECIPE(4, "Receipe"),
    SCROLL(5, "Scroll"),
    QUEST(6, "Quest"),
    MONEY(7, "Money"),
    OTHER(8, "Other"),
    SPELLBOOK(9, "Spellbook"),
    SEED(10, "Seed"),
    SHOT(11, "Shot"),
    HERB(12, "Herb");

    final int id;
    final String name;

    EtcItemType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
