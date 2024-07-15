package com.shnok.javaserver.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

@Config.Sources({
        "file:./conf/npc.properties",
        "classpath:conf/npc.properties"
})
@Config.LoadPolicy(MERGE)
@Config.HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface NpcConfig extends Mutable, Reloadable {

    @Config.Key("lvl.difference.dmg.penalty")
    @Config.Separator(",")
    Float[] lvlDifferenceDmgPenalty();
    @Config.Key("lvl.difference.crit.dmg.penalty")
    @Config.Separator(",")
    Float[] lvlDifferenceCritDmgPenalty();
    @Config.Key("lvl.difference.skill.dmg.penalty")
    @Config.Separator(",")
    Float[] lvlDifferenceSkillDmgPenalty();
    @Config.Key("lvl.difference.skill.chance.penalty")
    @Config.Separator(",")
    Float[] lvlDifferenceSkillChancePenalty();
}
