package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncPAtkSpeed extends AbstractFunction {
    private static final FuncPAtkSpeed _fas_instance = new FuncPAtkSpeed();

    public static AbstractFunction getInstance() {
        return _fas_instance;
    }

    private FuncPAtkSpeed() {
        super(Stats.POWER_ATTACK_SPEED, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        System.out.println("ATK SPEED: " + getValue());
        System.out.println("ATK SPEED: " + initVal);
        return initVal * Formulas.DEXbonus[effector.getDEX()];
    }
}
