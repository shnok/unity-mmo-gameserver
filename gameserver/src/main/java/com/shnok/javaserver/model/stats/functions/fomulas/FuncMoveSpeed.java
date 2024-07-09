package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncMoveSpeed extends AbstractFunction {
    private static final FuncMoveSpeed _fms_instance = new FuncMoveSpeed();

    public static AbstractFunction getInstance() {
        return _fms_instance;
    }

    private FuncMoveSpeed() {
        super(Stats.MOVE_SPEED, 1, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        return initVal * Formulas.DEXbonus[effector.getDEX()];
    }
}
