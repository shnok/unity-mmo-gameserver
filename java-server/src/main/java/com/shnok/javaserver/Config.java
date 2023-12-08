package com.shnok.javaserver;

import com.shnok.javaserver.model.Point3D;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;

public class Config {
    public static final String CONFIG_FILE = "server.properties";

    public static int GAMESERVER_PORT;
    public static int CONNECTION_TIMEOUT_SEC;
    public static int TIME_TICKS_PER_SECOND;
    public static boolean PRINT_SERVER_PACKETS;
    public static boolean PRINT_CLIENT_PACKETS;
    public static boolean SPAWN_NPCS;
    public static Point3D PLAYER_SPAWN_POINT;

    public static void LoadSettings() throws Exception {
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(CONFIG_FILE);

        Properties optionsSettings = new Properties();
        InputStream is = Files.newInputStream(new File(resourceUrl.getPath()).toPath());
        optionsSettings.load(is);
        is.close();

        GAMESERVER_PORT = Integer.parseInt(optionsSettings.getProperty("gameserver.port", "8000"));
        CONNECTION_TIMEOUT_SEC = Integer.parseInt(optionsSettings.getProperty("server.connection.timeout.ms", "10"));
        TIME_TICKS_PER_SECOND = Integer.parseInt(optionsSettings.getProperty("server.time.ticks-per-second", "10"));

        float spawnX = Float.parseFloat(optionsSettings.getProperty("server.spawn.location.x", "0"));
        float spawnY = Float.parseFloat(optionsSettings.getProperty("server.spawn.location.y", "0"));
        float spawnZ = Float.parseFloat(optionsSettings.getProperty("server.spawn.location.z", "0"));
        SPAWN_NPCS = Boolean.parseBoolean(optionsSettings.getProperty("server.world.spawn.npcs", "true"));

        PLAYER_SPAWN_POINT = new Point3D(spawnX, spawnY, spawnZ);

        PRINT_SERVER_PACKETS = Boolean.parseBoolean(optionsSettings.getProperty("logger.print.server-packets", "false"));
        PRINT_CLIENT_PACKETS = Boolean.parseBoolean(optionsSettings.getProperty("logger.print.client-packets", "false"));
    }
}
