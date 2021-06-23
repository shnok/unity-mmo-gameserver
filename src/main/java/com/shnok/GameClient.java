package com.shnok;

import com.shnok.model.EntityStatus;
import com.shnok.model.PlayerInstance;
import com.shnok.serverpackets.PlayerInfo;
import com.shnok.serverpackets.RemoveObjectPacket;
import com.shnok.serverpackets.SystemMessagePacket;

import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

public class GameClient extends GameServerThread {
    private ClientPacketHandler _cph;
    private String _username;
    private PlayerInstance _player;

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

    public PlayerInstance getCurrentPlayer() {
        return _player;
    }

    @Override
    void handlePacket(byte[] data) {
        _cph.handle(data);
    }

    @Override
    void authenticate() {
        Server.getInstance().broadcast(new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_IN, _username));
        _player = new PlayerInstance(World.getInstance().nextID(), _username);
        World.getInstance().addPlayer(_player);
        Server.getInstance().broadcast(new PlayerInfo(_player), this);
    }

    @Override
    void removeSelf() {
        if(authenticated) {
            Server.getInstance().broadcast(new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_OFF, _username), this);
            Server.getInstance().broadcast(new RemoveObjectPacket(_player.getId()), this);
            World.getInstance().removePlayer(_player);
        }

        Server.getInstance().removeClient(this);
    }
}
