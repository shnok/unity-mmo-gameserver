package com.shnok;

import com.shnok.serverpackets.ServerPacket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public final int PORT = 11000;
    private final GameServerListener _gameServerListener;
    private final List<GameClient> _clients = new ArrayList<>();

    private static Server _instance;

    public static Server getInstance() {
        if(_instance == null) {
            _instance = new Server();
        }
        return _instance;
    }

    public Server() {
        _gameServerListener = new GameServerListener(PORT);
    }

    public static void main(String[] av) {
        Server s = getInstance();
        s.getGameServerListener().start();
    }

    public GameServerListener getGameServerListener() {
        return _gameServerListener;
    }

    public void addClient(Socket s) {
        GameClient client = new GameClient(s);
        client.start();
        _clients.add(client);
    }

    public void removeClient(GameClient s) {
        synchronized (_clients) {
            _clients.remove(s);
        }
    }

    public void broadcast(ServerPacket packet) {
        synchronized (_clients) {
            for(GameClient c : _clients) {
                if(c.authenticated) {
                    c.sendPacket(packet);
                }
            }
        }
    }

    public boolean userExists(String user) {
        synchronized (_clients) {
            for(GameClient c : _clients) {
                if(c.authenticated) {
                    return c.getUsername().equals(user);
                }
            }

            return false;
        }
    }
}
