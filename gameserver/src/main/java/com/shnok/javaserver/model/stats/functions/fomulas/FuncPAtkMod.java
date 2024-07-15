package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncPAtkMod extends AbstractFunction {
    private static final FuncPAtkMod _fpa_instance = new FuncPAtkMod();

    public static AbstractFunction getInstance() {
        return _fpa_instance;
    }

    private FuncPAtkMod() {
        super(Stats.POWER_ATTACK, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        return initVal * Formulas.STRbonus[effector.getSTR()] * effector.getLevelMod();
    }
}
