package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncPAtkCritical extends AbstractFunction {
    private static final FuncPAtkCritical _fac_instance = new FuncPAtkCritical();

    public static AbstractFunction getInstance() {
        return _fac_instance;
    }

    private FuncPAtkCritical() {
        super(Stats.CRITICAL_RATE, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        System.out.println("FuncPAtkCritical INIT VAL: " + initVal);
        System.out.println("FuncPAtkCritical getValue: " + getValue());
        System.out.println("FuncPAtkCritical VAL BONUS MULTIPLIER: " + Formulas.DEXbonus[effector.getDEX()]);

        if ((effector.isPlayer()) && (effector.getActiveWeaponInstance() == null)) {
            return initVal;
        }

        return initVal * Formulas.DEXbonus[effector.getDEX()] * 10;
    }
}
