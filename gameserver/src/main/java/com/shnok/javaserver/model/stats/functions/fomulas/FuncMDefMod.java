package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.skills.Skill;
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
    public double calc(Entity effector, Entity effected, Skill skill, double initVal) {
        double value = initVal;
        if (effector.isPlayer()) {
            PlayerInstance p = effector.getActingPlayer();
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_LFINGER)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_LFINGER) : Inventory.PAPERDOLL_LFINGER);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_RFINGER)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_RFINGER) : Inventory.PAPERDOLL_RFINGER);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_LEAR)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_LEAR) : Inventory.PAPERDOLL_LEAR);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_REAR)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_REAR) : Inventory.PAPERDOLL_REAR);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_NECK)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_NECK) : Inventory.PAPERDOLL_NECK);
            }
        } else if (effector.isPet() && (effector.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK) != 0)) {
            value -= 13;
        }
        return value * BaseStats.MEN.calcBonus(effector) * effector.getLevelMod();
    }
}