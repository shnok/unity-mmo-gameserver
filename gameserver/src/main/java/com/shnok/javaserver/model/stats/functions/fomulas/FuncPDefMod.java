package com.shnok.javaserver.model.stats.functions.fomulas;

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
    public double calc(Entity effector, Entity effected, Skill skill, double initVal) {
        double value = initVal;
        if (effector.isPlayer()) {
            final PlayerInstance p = (PlayerInstance) effector;
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_CHEST)) {
                value -= p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_CHEST) : p.getTemplate().getBaseDefBySlot(Inventory.PAPERDOLL_CHEST);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_LEGS) || (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_CHEST) && (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItem().getBodyPart() == DBItem.SLOT_FULL_ARMOR))) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_LEGS) : Inventory.PAPERDOLL_LEGS);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_HEAD)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_HEAD) : Inventory.PAPERDOLL_HEAD);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_FEET)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_FEET) : Inventory.PAPERDOLL_FEET);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_GLOVES)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_GLOVES) : Inventory.PAPERDOLL_GLOVES);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_UNDER)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_UNDER) : Inventory.PAPERDOLL_UNDER);
            }
            if (!p.getInventory().isPaperdollSlotEmpty(Inventory.PAPERDOLL_CLOAK)) {
                value -= p.getTemplate().getBaseDefBySlot(p.isTransformed() ? p.getTransformation().getBaseDefBySlot(p, Inventory.PAPERDOLL_CLOAK) : Inventory.PAPERDOLL_CLOAK);
            }
        }
        return value * effector.getLevelMod();
    }
}
