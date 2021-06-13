package com.shnok;

public class Server {

    public static final int ECHOPORT = 11000;
    private static GameServerListener _gameServerListener;

    public static void main(String[] av) {
        _gameServerListener = new GameServerListener(ECHOPORT);
    }

    public static GameServerListener getGameServerListener() {
        return _gameServerListener;
    }
}
