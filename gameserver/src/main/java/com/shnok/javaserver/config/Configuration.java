package com.shnok.javaserver.config;

import lombok.Getter;
import org.aeonbits.owner.ConfigCache;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.logging.log4j.core.util.Loader.getClassLoader;

@Getter
public class Configuration {
    public static final String DEFAULT_PATH = "conf/";

    public static final HexIdConfig hexId =  ConfigCache.getOrCreate(HexIdConfig.class);
    public static final ServerConfig server =  ConfigCache.getOrCreate(ServerConfig.class);
    public static final CharacterConfig character =  ConfigCache.getOrCreate(CharacterConfig.class);
    public static final NpcConfig npc =  ConfigCache.getOrCreate(NpcConfig.class);
    public static final RatesConfig rates =  ConfigCache.getOrCreate(RatesConfig.class);

    public static String getDefaultPath(String filename) {
        return DEFAULT_PATH + filename;
    }
}

