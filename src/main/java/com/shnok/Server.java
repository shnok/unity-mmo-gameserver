package com.shnok;

public class Server {

    public static final int PORT = 11000;
    private static GameServerListener _gameServerListener;

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
}
