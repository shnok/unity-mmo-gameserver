package com.shnok.javaserver.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

@Config.Sources({
        "file:./conf/rates.properties",
        "classpath:conf/rates.properties"
})
@Config.LoadPolicy(MERGE)
@Config.HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface RatesConfig extends Mutable, Reloadable {

    @Config.Key("rate.xp")
    Integer ratesXp();
    @Config.Key("rate.sp")
    Integer ratesSp();
    @Config.Key("rate.party.xp")
    Integer ratePartyXp();
    @Config.Key("rate.party.sp")
    Integer ratePartySp();
    @Config.Key("rate.karma.lost")
    Integer rateKarmaLost();
    @Config.Key("rate.karma.exp.lost")
    Integer rateKarmaExpLost();
}