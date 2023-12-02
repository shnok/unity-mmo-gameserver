package com.shnok.javaserver.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Service
@Log4j2
public class GameServerListenerService extends Thread {
    public static int TIMEOUT_MS;
    private final int port;
    private final ServerSocket serverSocket;
    @Autowired
    private ServerService serverService;

    public GameServerListenerService(@Value("${gameserver.port}") int port,
                                     @Value("${server.connection.timeout.ms}") int connectionTimeoutMs) {
        try {
            serverSocket = new ServerSocket(port);
            this.port = port;
            TIMEOUT_MS = connectionTimeoutMs;
        } catch (IOException e) {
            throw new RuntimeException("Could not create ServerSocket ", e);
        }
    }

    @Override
    public void run() {
        log.info("Server listening on port {}. ", port);
        while (true) {
            Socket connection = null;
            try {
                connection = serverSocket.accept();
                serverService.addClient(connection);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                if (isInterrupted()) {
                    try {
                        serverSocket.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
