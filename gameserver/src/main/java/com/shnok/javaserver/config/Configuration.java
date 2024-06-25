package com.shnok.javaserver.config;

import lombok.Getter;
import org.aeonbits.owner.ConfigCache;

@Getter
public class Configuration {
    public static final ServerConfig server =  ConfigCache.getOrCreate(ServerConfig.class);
    public static final HexIdConfig hexId =  ConfigCache.getOrCreate(HexIdConfig.class);
}

