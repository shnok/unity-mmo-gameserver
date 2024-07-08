package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncAtkCritical extends AbstractFunction {
    private static final FuncAtkCritical _fac_instance = new FuncAtkCritical();

    public static AbstractFunction getInstance() {
        return _fac_instance;
    }

    private FuncAtkCritical() {
        super(Stats.CRITICAL_RATE, 1, null, 0, null);
    }

    @Override
    public double calc(Entity effector, Entity effected, Skill skill, double initVal) {
        return initVal * Formulas.DEXbonus[effector.getDEX()] * 10;
    }
}
