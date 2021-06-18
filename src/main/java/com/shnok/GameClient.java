package com.shnok;

import java.net.Socket;

public class GameClient extends GameServerThread {
    private GamePacketHandler _gph;
    private String _username;

    public GameClient(Socket con) {
        super(con);
        _gph = new GamePacketHandler(this);
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }

    @Override
    void handlePacket(byte type, byte[] data) {
        _gph.handle(type, data);
    }

    @Override
    void removeSelf() {
        Server.removeClient(this);
    }
}
