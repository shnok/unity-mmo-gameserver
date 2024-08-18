package com.shnok.javaserver.model.stats.functions.fomulas;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

public class FuncMAtkEvasion extends AbstractFunction {
    /*
    	@Override
	public double calc(Creature creature, OptionalDouble base, Stat stat)
	{
		throwIfPresent(base);

		double baseValue = calcWeaponPlusBaseValue(creature, stat);

		final int level = creature.getLevel();
		if (creature.isPlayer())
		{
			// [Square(WIT)] * 3 + level;
			baseValue += (Math.sqrt(creature.getWIT()) * 3) + (level * 2);

			// Enchanted helm bonus
			baseValue += calcEnchantBodyPart(creature, ItemTemplate.SLOT_HEAD);
		}
		else
		{
			// [Square(DEX)] * 6 + level;
			baseValue += (Math.sqrt(creature.getWIT()) * 3) + (level * 2);
			if (level > 69)
			{
				baseValue += (level - 69) + 2;
			}
		}
		return validateValue(creature, Stat.defaultValue(creature, stat, baseValue), Double.NEGATIVE_INFINITY, creature.isPlayable() ? Config.MAX_EVASION : Double.MAX_VALUE);
	}
     */

    private static final FuncMAtkEvasion _fae_instance = new FuncMAtkEvasion();

    public static AbstractFunction getInstance() {
        return _fae_instance;
    }

    private FuncMAtkEvasion() {
        super(Stats.MAGIC_EVASION_RATE, 0x10, null, 0, null);
    }

    @Override
    public float calc(Entity effector, Entity effected, Skill skill, float initVal) {
        final int level = effector.getLevel();
        float value = initVal;

        if (effector.isPlayer()) {
            // [Square(WIT)] * 3 + level;
            value += (Math.sqrt(effector.getWIT()) * 3) + (level * 2);
        } else {
            // [Square(DEX)] * 6 + level;
            value += (Math.sqrt(effector.getWIT()) * 3) + (level * 2);
            if (level > 69) {
                value += (level - 69) + 2;
            }
        }
        return (int) value;
    }
}
