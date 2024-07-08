package com.shnok.javaserver.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

@Config.Sources({
        "file:./conf/character.properties",
        "classpath:conf/character.properties"
})
@Config.LoadPolicy(MERGE)
@Config.HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface CharacterConfig extends Mutable, Reloadable {
    @Key("run.speed.boost")
    Integer runSpeedBoost();
    @Key("respawn.restore.cp")
    Float respawnRestoreCP();
    @Key("respawn.restore.hp")
    Float respawnRestoreHP();
    @Key("respawn.restore.mp")
    Float respawnRestoreMP();
    @Key("hp.regen.multiplier")
    Float hpRegenMultiplier();
    @Key("mp.regen.multiplier")
    Float mpRegenMultiplier();
    @Key("cp.regen.multiplier")
    Float cpRegenMultiplier();
    @Key("shield.blocks")
    Boolean shieldBlocks();
    @Key("shield.perfect.block.rate")
    Integer shieldPerfectBlockRate();
}
