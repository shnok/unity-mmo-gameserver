package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncAtkEvasion extends AbstractFunction {
    private static final FuncAtkEvasion _fae_instance = new FuncAtkEvasion();

    public static AbstractFunction getInstance() {
        return _fae_instance;
    }

    private FuncAtkEvasion() {
        super(Stats.EVASION_RATE, 1, null, 0, null);
    }

    @Override
    public double calc(Entity effector, Entity effected, Skill skill, double initVal) {
        final int level = effector.getStatus().getLevel();
        double value = initVal;
        if (effector.isPlayer()) {
            // [Square(DEX)] * 6 + lvl;
            value += (Math.sqrt(effector.getDEX()) * 6) + level;
            double diff = level - 69;
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
