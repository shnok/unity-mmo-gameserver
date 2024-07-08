package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncMaxMpMul extends AbstractFunction {
    private static final FuncMaxMpMul _fmmm_instance = new FuncMaxMpMul();

    public static AbstractFunction getInstance() {
        return _fmmm_instance;
    }

    private FuncMaxMpMul() {
        super(Stats.MAX_MP, 1, null, 0, null);
    }

    @Override
    public double calc(Entity effector, Entity effected, Skill skill, double initVal) {
        return initVal * BaseStats.MEN.calcBonus(effector);
    }
}