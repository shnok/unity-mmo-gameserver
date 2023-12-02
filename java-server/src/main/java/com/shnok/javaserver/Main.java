package com.shnok.javaserver;

import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class Main implements CommandLineRunner {
    @Autowired
    private ServerService serverService;
    @Autowired
    private DatabaseMockupService databaseMockupService;
    @Autowired
    private SpawnManagerService spawnManagerService;
    @Autowired
    private GameServerListenerService gameServerListenerService;
    @Autowired
    private ThreadPoolManagerService threadPoolManagerService;
    @Autowired
    private ServerShutdownService serverShutdownService;
    @Autowired
    private GameTimeControllerService gameTimeControllerService;
    @Autowired
    private WorldManagerService worldManagerService;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting application.");
        Runtime.getRuntime().addShutdownHook(serverShutdownService);

        //TODO: Update for gradle and new geodata structure
        Geodata.getInstance();
        PathFinding.getInstance();

        worldManagerService.initialize();
        gameTimeControllerService.initialize();
        threadPoolManagerService.initialize();
        databaseMockupService.initialize();
        spawnManagerService.initialize();

        gameServerListenerService.start();
        try {
            gameServerListenerService.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Application closed");
    }
}
