package com.shnok;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServerListener extends Thread {
    private final int _port;
    private ServerSocket _serverSocket;
    private static List<GameServerThread> _gameServers = new ArrayList<>();

    public GameServerListener(int port) {
        try {
            _serverSocket = new ServerSocket(port);
            _port = port;
            start();
        } catch(IOException e) {
            throw new RuntimeException("Could not create ServerSocket ", e);
        }
    }

    @Override
    public void run() {
        Socket connection = null;
        System.out.println("Listening on port " + _port);

        while (true) {
            try {
                connection = _serverSocket.accept();
                addClient(connection);
            } catch (Exception e) {
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

    public void addClient(Socket s)
    {
        GameServerThread gst = new GameServerThread(s);
        _gameServers.add(gst);
    }

    public void removeGameServer(GameServerThread s) {
        _gameServers.remove(s);
    }
}
