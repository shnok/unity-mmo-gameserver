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
    @Key("max.buff.amount")
    Integer maxBuffAmount();
    @Key("max.exp.bonus")
    float getMaxExpBonus();
    @Key("max.sp.bonus")
    float getMaxSpBonus();
    @Key("max.run.speed")
    Integer getMaxRunSpeed();
    @Key("max.pcrit.rate")
    Integer getMaxPCritRate();
    @Key("max.mcrit.rate")
    Integer getMaxMCritRate();
    @Key("max.patk.speed")
    Integer getMaxPAtkSpeed();
    @Key("max.matk.speed")
    Integer getMaxMAtkSpeed();
    @Key("max.evasion")
    Integer getMaxEvasion();
    @Key("min.abnormal.success.rate")
    Integer getMinAbnormalStateSuccessRate();
    @Key("max.abnormal.success.rate")
    Integer getMaxAbnormalStateSuccessRate();
    @Key("max.player.level")
    Integer getMaxPlayerLevel();
    @Key("magic.resists")
    Boolean magicResists();
    @Key("cancel.bow")
    Boolean cancelBow();
    @Key("cancel.cast")
    Boolean cancelCast();
}
