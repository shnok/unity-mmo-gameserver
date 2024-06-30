package com.shnok.javaserver.config;

import org.aeonbits.owner.Config.HotReload;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

import java.util.Set;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

@Sources({
        "file:./conf/server.properties",
        "classpath:conf/server.properties"
})
@LoadPolicy(MERGE)
@HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface ServerConfig extends Mutable, Reloadable {
    // Connection
    @Key("gameserver.port")
    Integer gameserverPort();
    @Key("server.connection.timeout.ms")
    Integer serverConnectionTimeoutMs();
    @Key("loginserver.host")
    String loginServerHost();
    @Key("loginserver.port")
    Integer loginServerPort();
    @Key("request.server.id")
    Integer requestServerId();
    @Key("accept.alternate.id")
    Boolean acceptAlternateId();
    @Key("max.online.user")
    Integer maxOnlineUser();
    @Key("allowed.protocol.versions")
    Set<Integer> allowedProtocolVersions();

    // Security
    @Key("rsa.padding.mode")
    String rsaPaddingMode();

    // Administrator
    @Key("server.gm.only")
    Boolean serverGMOnly();
    @Key("create.random.character")
    Boolean createRandomCharacter();

    // Timer
    @Key("server.time.day.duration.minutes")
    Integer dayDurationMin();
    @Key("server.time.ticks-per-second")
    Integer serverTicksPerSecond();

    // Spawn
    @Key("server.spawn.location.x")
    Float spawnLocationX();
    @Key("server.spawn.location.y")
    Float spawnLocationY();
    @Key("server.spawn.location.z")
    Float spawnLocationZ();

    // World
    @Key("server.world.npc.spawn-npcs")
    Boolean spawnNpcs();
    @Key("server.world.npc.spawn-monsters")
    Boolean spawnMonsters();
    @Key("server.world.npc.spawn-debug")
    Boolean spawnDebug();

    // Item
    @Key("server.item.money.id")
    Integer itemMoneyId();

    // Player
    @Key("server.world.player.specific-character")
    Boolean playerSpecificCharacterEnabled();
    @Key("server.world.player.specific-character-id")
    Integer playerSpecificCharacterId();

    // AI
    @Key("server.ai.loop-rate-ms")
    Integer aiLoopRateMs();
    @Key("server.ai.keep-alive")
    Boolean aiKeepAlive();
    @Key("server.ai.monsters.patrol")
    Boolean aiMonstersPatrolEnabled();
    @Key("server.ai.monsters.patrol-chance")
    Integer aiMonstersPatrolChance();
    @Key("server.ai.monsters.patrol-distance")
    Integer aiMonstersPatrolDistance();

    // Geodata
    @Key("server.world.geodata.map-size")
    Integer geodataMapSize();
    @Key("server.world.geodata.node-size")
    Float geodataNodeSize();
    @Key("server.world.geodata.zones.load")
    @Separator(",")
    String[] geodataZonesToLoad();
    @Key("server.world.geodata.maximum-y-error")
    Integer geodataMaximumYError();
    @Key("server.world.geodata.pathfinder.enabled")
    Boolean geodataPathFinderEnabled();
    @Key("server.world.geodata.total-layers")
    Integer geodataTotalLayers();
    @Key("server.world.geodata.pathfinder.simplify-path")
    Boolean geodataPathFinderSimplifyPath();

    // Logger
    @Key("logger.print.server-packets")
    Boolean printServerPackets();
    @Key("logger.print.client-packets")
    Boolean printClientPackets();
    @Key("logger.print.cryptography")
    Boolean printCryptography();
    @Key("logger.print.pathfinder")
    Boolean printPathfinder();
    @Key("logger.print.world-region")
    Boolean printWorldRegion();
    @Key("logger.print.known-list")
    Boolean printKnownList();
    @Key("logger.print.ai")
    Boolean printAi();
}
