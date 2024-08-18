package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncPAtkEvasion extends AbstractFunction {
    private static final FuncPAtkEvasion _fae_instance = new FuncPAtkEvasion();

    public static AbstractFunction getInstance() {
        return _fae_instance;
    }

    private FuncPAtkEvasion() {
        super(Stats.POWER_EVASION_RATE, 0x10, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        final int level = effector.getLevel();
        float value = initVal;
        if (effector.isPlayer()) {
            // [Square(DEX)] * 6 + lvl;
            value += (Math.sqrt(effector.getDEX()) * 6) + level;
            float diff = level - 69;
            if (level >= 78) {
                diff *= 1.2;
            }
            if (level >= 70) {
                value += diff;
            }
        } else {
            // [Square(DEX)] * 6 + lvl;
            value += (Math.sqrt(effector.getDEX()) * 6) + level;
            if (level > 69) {
                value += (level - 69) + 2;
            }
        }
        return (int) value;
    }
}
