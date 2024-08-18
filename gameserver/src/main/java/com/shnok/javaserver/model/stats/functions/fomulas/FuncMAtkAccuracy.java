package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncMAtkAccuracy extends AbstractFunction {
    /*
    	@Override
	public double calc(Creature creature, OptionalDouble base, Stat stat)
	{
		throwIfPresent(base);

		double baseValue = calcWeaponPlusBaseValue(creature, stat);
		if (creature.isPlayer())
		{
			// Enchanted gloves bonus
			baseValue += calcEnchantBodyPart(creature, ItemTemplate.SLOT_GLOVES);
		}
		return Stat.defaultValue(creature, stat, baseValue + (Math.sqrt(creature.getWIT()) * 3) + (creature.getLevel() * 2));
	}

     */
    private static final FuncMAtkAccuracy _faa_instance = new FuncMAtkAccuracy();

    public static AbstractFunction getInstance() {
        return _faa_instance;
    }

    private FuncMAtkAccuracy() {
        super(Stats.MAGIC_ACCURACY_COMBAT, 0x10, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        final int level = effector.getLevel();
        // [Square(DEX)] * 6 + lvl + weapon hitbonus;
        float value = (float) (initVal + (Math.sqrt(effector.getWIT()) * 3) + (level * 2));
        // weapon accuracy ?
        return value;
    }
}