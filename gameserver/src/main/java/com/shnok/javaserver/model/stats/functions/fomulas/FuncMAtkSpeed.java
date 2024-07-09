package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncMAtkSpeed extends AbstractFunction {
    private static final FuncMAtkSpeed _fas_instance = new FuncMAtkSpeed();

    public static AbstractFunction getInstance() {
        return _fas_instance;
    }

    private FuncMAtkSpeed() {
        super(Stats.MAGIC_ATTACK_SPEED, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        return initVal * Formulas.WITbonus[effector.getWIT()];
    }
}
