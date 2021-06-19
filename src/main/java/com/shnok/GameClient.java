package com.shnok;

import com.shnok.serverpackets.SystemMessagePacket;

import java.net.Socket;

public class GameClient extends GameServerThread {
    private ClientPacketHandler _cph;
    private String _username;

    public GameClient(Socket con) {
        super(con);
        _cph = new ClientPacketHandler(this);
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }

    @Override
    void handlePacket(byte type, byte[] data) {
        _cph.handle(type, data);
    }

    @Override
    void removeSelf() {
        if(authenticated) {
            Server.broadcast(new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_OFF, _username));
        }

        Server.removeClient(this);
    }
}
