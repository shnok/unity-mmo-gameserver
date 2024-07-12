package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncPAtkAccuracy extends AbstractFunction {
    private static final FuncPAtkAccuracy _faa_instance = new FuncPAtkAccuracy();

    public static AbstractFunction getInstance() {
        return _faa_instance;
    }

    private FuncPAtkAccuracy() {
        super(Stats.POWER_ACCURACY_COMBAT, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        final int level = effector.getLevel();
        // [Square(DEX)] * 6 + lvl + weapon hitbonus;
        float value = (float) (initVal + (Math.sqrt(effector.getDEX()) * 6) + level);
        if (level > 77) {
            value += level - 76;
        }
        if (level > 69) {
            value += level - 69;
        }
        return value;
    }
}