package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncMaxHpMul extends AbstractFunction {
    private static final FuncMaxHpMul _fmhm_instance = new FuncMaxHpMul();

    public static AbstractFunction getInstance() {
        return _fmhm_instance;
    }

    private FuncMaxHpMul() {
        super(Stats.MAX_HP, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        System.out.println("Calc!");
        System.out.println("initVal: " + initVal);
        System.out.println("CON: " + effector.getCON());
        System.out.println("Multiplier: " + Formulas.CONbonus[effector.getCON()]);
        System.out.println("Result: " + initVal * Formulas.CONbonus[effector.getCON()]);
        return initVal * Formulas.CONbonus[effector.getCON()];
    }
}
