package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncAtkAccuracy extends AbstractFunction {
    private static final FuncAtkAccuracy _faa_instance = new FuncAtkAccuracy();

    public static AbstractFunction getInstance() {
        return _faa_instance;
    }

    private FuncAtkAccuracy() {
        super(Stats.ACCURACY_COMBAT, 1, null, 0, null);
    }

    @Override
    public double calc(Entity effector, Entity effected, Skill skill, double initVal) {
        final int level = effector.getLevel();
        // [Square(DEX)] * 6 + lvl + weapon hitbonus;
        double value = initVal + (Math.sqrt(effector.getDEX()) * 6) + level;
        if (level > 77) {
            value += level - 76;
        }
        if (level > 69) {
            value += level - 69;
        }
        return value;
    }
}