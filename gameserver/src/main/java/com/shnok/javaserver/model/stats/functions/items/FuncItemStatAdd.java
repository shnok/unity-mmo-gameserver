package com.shnok.javaserver.model.stats.functions.items;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;
import com.shnok.javaserver.model.stats.functions.Condition;

public class FuncItemStatAdd extends AbstractFunction {
    /**
     * Constructor of Func.
     *
     * @param stat      the stat
     * @param order     the order
     * @param owner     the owner
     * @param value     the value
     * @param applyCond the apply condition
     */
    public FuncItemStatAdd(Stats stat, int order, Object owner, float value, Condition applyCond) {
        super(stat, order, owner, value, applyCond);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        return initVal + getValue();
    }
}
