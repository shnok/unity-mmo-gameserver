package com.shnok;

import java.net.Socket;

public class GameClient extends GameServerThread {
    private GamePacketHandler _gph;

    public GameClient(Socket con) {
        super(con);
        _gph = new GamePacketHandler(this);
    }

    @Override
    void handlePacket(byte type, byte[] data) {
        _gph.handle(type, data);
    }

    @Override
    void removeSelf() {
        Server.getGameServerListener().removeClient(this);
    }
}
