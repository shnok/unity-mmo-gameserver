package com.shnok;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static final int PORT = 11000;
    private static GameServerListener _gameServerListener;
    private static final List<GameClient> _clients = new ArrayList<>();

    public Server() {
        _gameServerListener = new GameServerListener(PORT);
        _gameServerListener.run();
    }

    public static void main(String[] av) {
        Server server = new Server();
    }

    public static GameServerListener getGameServerListener() {
        return _gameServerListener;
    }

    public static void addClient(Socket s) {
        GameClient client = new GameClient(s);
        client.start();
        _clients.add(client);
    }

    public static void removeClient(GameClient s) {
        synchronized (_clients) {
            _clients.remove(s);
        }
    }
}
