package com.shnok.javaserver.model;

import com.shnok.javaserver.model.object.GameObject;
import lombok.Getter;

@Getter
public class Hit {
    private static final int HITFLAG_USESS = 0x10;
    private static final int HITFLAG_CRIT = 0x20;
    private static final int HITFLAG_SHLD = 0x40;
    private static final int HITFLAG_MISS = 0x80;

    private final int targetId;
    private final int damage;
    private int flags = 0;

    public Hit(GameObject target, int damage, boolean miss, boolean crit, byte shld, boolean soulshot, int ssGrade) {
        targetId = target.getId();
        this.damage = damage;

        if (soulshot) {
            flags |= HITFLAG_USESS | ssGrade;
        }

        if (crit) {
            flags |= HITFLAG_CRIT;
        }

        if (shld > 0) {
            flags |= HITFLAG_SHLD;
        }

        if (miss) {
            flags |= HITFLAG_MISS;
        }
    }
}
