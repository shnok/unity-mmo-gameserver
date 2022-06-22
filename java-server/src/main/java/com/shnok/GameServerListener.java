package com.shnok;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServerListener extends Thread {
    private final int _port;
    private final ServerSocket _serverSocket;

    public GameServerListener(int port) {
        try {
            System.out.println("Create gameserverlistener");
            _serverSocket = new ServerSocket(port);
            _port = port;
        } catch (IOException e) {
            throw new RuntimeException("Could not create ServerSocket ", e);
        }
    }

    @Override
    public void run() {
        System.out.println("Listening on port " + _port);
        while (true) {
            Socket connection = null;
            try {
                connection = _serverSocket.accept();
                Server.getInstance().addClient(connection);
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
                        _serverSocket.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
