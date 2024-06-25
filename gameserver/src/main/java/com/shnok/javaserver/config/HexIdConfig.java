package com.shnok.javaserver.config;

import com.shnok.javaserver.config.Converter.HexIdConverter;
import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.HotReload;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

import java.math.BigInteger;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

@Sources({
        "file:./conf/" + HexIdConfig.FILENAME,
        "classpath:conf/" + HexIdConfig.FILENAME
})
@Config.LoadPolicy(MERGE)
@HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface HexIdConfig extends Mutable, Reloadable, Accessible {
    String FILENAME = "hexid.txt";

    String SERVERID_KEY = "ServerID";
    String HEXID_KEY = "HexID";

    @Key(SERVERID_KEY)
    Integer getServerID();

    @Key(HEXID_KEY)
    @ConverterClass(HexIdConverter.class)
    BigInteger getHexID();
}
