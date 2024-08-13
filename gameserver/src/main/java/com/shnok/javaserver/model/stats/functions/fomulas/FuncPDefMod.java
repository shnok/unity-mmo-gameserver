package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncPDefMod extends AbstractFunction {
    private static final FuncPDefMod _fmm_instance = new FuncPDefMod();

    public static AbstractFunction getInstance() {
        return _fmm_instance;
    }

    private FuncPDefMod() {
        super(Stats.POWER_DEFENCE, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        float value = initVal;
        if (effector.isPlayer()) {
            final PlayerInstance p = (PlayerInstance) effector;
            if (p.getInventory().isSlotUsed(ItemSlot.head)) {
                value -= 12;
            }
            if (p.getInventory().isSlotUsed(ItemSlot.chest)) {
                value -= ((p.getTemplate().getClassId().isMage()) ? 15 : 31);
            }
            if (p.getInventory().isSlotUsed(ItemSlot.legs)) {
                value -= ((p.getTemplate().getClassId().isMage()) ? 8 : 18);
            }
            if (p.getInventory().isSlotUsed(ItemSlot.gloves)) {
                value -= 8;
            }
            if (p.getInventory().isSlotUsed(ItemSlot.feet)) {
                value -= 7;
            }
        }
        return value * effector.getLevelMod();
    }
}
