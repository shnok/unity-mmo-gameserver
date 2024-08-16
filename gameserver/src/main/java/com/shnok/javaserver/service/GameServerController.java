package com.shnok.javaserver.service;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.extern.log4j.Log4j2;

import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class GameServerController {
    private static GameServerController instance;
    public static GameServerController getInstance() {
        if (instance == null) {
            instance = new GameServerController();
        }
        return instance;
    }

    private final List<GameClientThread> clients = new ArrayList<>();

    public void addClient(Socket socket) throws SocketException {
        GameClientThread client = new GameClientThread(socket);
        client.start();
        clients.add(client);
    }

    public void removeClient(GameClientThread s) {
        synchronized (clients) {
            clients.remove(s);
        }
    }

    public List<GameClientThread> getAllClients() {
        return clients;
    }

    // Broadcast to everyone ignoring caller
    public void broadcast(SendablePacket packet, GameClientThread current) {
        synchronized (clients) {
            for (GameClientThread c : clients) {
                if (c.getGameClientState() == GameClientState.IN_GAME && c != current) {
                    c.sendPacket(packet);
                }
            }
        }
    }

   // Broadcast to everyone
   public void broadcast(SendablePacket packet) {
        synchronized (clients) {
            for (GameClientThread c : clients) {
                if (c.getGameClientState() == GameClientState.IN_GAME) {
                    c.sendPacket(packet);
                }
            }
        }
    }
}
