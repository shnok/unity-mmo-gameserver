package com.shnok.javaserver;

import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.service.*;
import com.shnok.javaserver.service.db.ItemTableService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    public static void main(String[] args) {
       runServer(args);
    }

    public static void runServer(String... args)  {
        log.info("Starting application.");
        try {
            //Config.initializeLog4j();
            Config.loadConfig();
        } catch (Exception e) {
            log.error("Error while loading config file.", e);
            return;
        }

        ThreadPoolManagerService.getInstance().initialize();
        Runtime.getRuntime().addShutdownHook(ServerShutdownService.getInstance());

        ItemTableService.getInstance();

        Geodata.getInstance().loadGeodata();
        PathFinding.getInstance();

        WorldManagerService.getInstance().initialize();
        GameTimeControllerService.getInstance().initialize();
        SpawnManagerService.getInstance().initialize();

        GameServerListenerService.getInstance().Initialize();
        GameServerListenerService.getInstance().start();
        try {
            GameServerListenerService.getInstance().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Application closed");
    }
}
