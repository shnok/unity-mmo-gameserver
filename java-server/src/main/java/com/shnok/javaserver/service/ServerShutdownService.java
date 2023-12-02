package com.shnok.javaserver.service;

import com.shnok.javaserver.thread.GameClientThread;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ServerShutdownService extends Thread {
    @Autowired
    private ServerService serverService;
    @Autowired
    private GameTimeControllerService gameTimeControllerService;
    @Autowired
    private ThreadPoolManagerService threadPoolManagerService;
    @Autowired
    private GameServerListenerService gameServerListenerService;

    @Autowired
    public ServerShutdownService() {
    }

    @Override
    public void run() {
        log.info("Shutting down all threads.");
        try {
            gameServerListenerService.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (GameClientThread c : serverService.getAllClients()) {
                c.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            threadPoolManagerService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            gameTimeControllerService.stopTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
