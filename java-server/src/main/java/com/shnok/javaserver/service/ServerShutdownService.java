package com.shnok.javaserver.service;

import com.shnok.javaserver.thread.GameClientThread;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ServerShutdownService extends Thread {
    private static ServerShutdownService instance;
    public static ServerShutdownService getInstance() {
        if (instance == null) {
            instance = new ServerShutdownService();
        }
        return instance;
    }


    @Override
    public void run() {
        log.info("Shutting down all threads.");
        try {
            GameServerListenerService.getInstance().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (GameClientThread c : ServerService.getInstance().getAllClients()) {
                c.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ThreadPoolManagerService.getInstance().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            GameTimeControllerService.getInstance().stopTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
