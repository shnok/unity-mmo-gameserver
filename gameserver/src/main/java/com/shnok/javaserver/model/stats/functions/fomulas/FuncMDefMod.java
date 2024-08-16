package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncMDefMod extends AbstractFunction {
    private static final FuncMDefMod _fmm_instance = new FuncMDefMod();

    public static AbstractFunction getInstance() {
        return _fmm_instance;
    }

    private FuncMDefMod() {
        super(Stats.MAGIC_DEFENCE, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        float value = initVal;
        if (effector.isPlayer()) {
            PlayerInstance p = (PlayerInstance) effector;
            if (p.getInventory().isSlotUsed(ItemSlot.lfinger)) {
                value -= 5;
            }
            if (p.getInventory().isSlotUsed(ItemSlot.rfinger)) {
                value -= 5;
            }
            if (p.getInventory().isSlotUsed(ItemSlot.lear)) {
                value -= 9;
            }
            if (p.getInventory().isSlotUsed(ItemSlot.rear)) {
                value -= 9;
            }
            if (p.getInventory().isSlotUsed(ItemSlot.neck)) {
                value -= 13;
            }
        }

        return value * Formulas.MENbonus[effector.getMEN()] * effector.getLevelMod();
    }
}