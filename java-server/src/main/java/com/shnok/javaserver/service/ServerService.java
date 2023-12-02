package com.shnok.javaserver.service;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class ServerService {
    @Autowired
    private WorldManagerService worldManagerService;
    @Autowired
    private ThreadPoolManagerService threadPoolManagerService;
    @Autowired
    private DatabaseMockupService databaseMockupService;

    private final List<GameClientThread> clients = new ArrayList<>();

    @Autowired
    public ServerService() {
    }

    public void addClient(Socket socket) {
        GameClientThread client = new GameClientThread(
                this,
                worldManagerService,
                threadPoolManagerService,
                databaseMockupService,
                socket);
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

    public void broadcast(ServerPacket packet) {
        synchronized (clients) {
            for (GameClientThread c : clients) {
                if (c.authenticated) {
                    c.sendPacket(packet);
                }
            }
        }
    }

    public void broadcast(ServerPacket packet, GameClientThread current) {
        synchronized (clients) {
            for (GameClientThread c : clients) {
                if (c.authenticated && c != current) {
                    c.sendPacket(packet);
                }
            }
        }
    }

    public void broadcastAll(ServerPacket packet) {
        synchronized (clients) {
            for (GameClientThread c : clients) {
                if (c.authenticated) {
                    c.sendPacket(packet);
                }
            }
        }
    }

    public boolean userExists(String user) {
        synchronized (clients) {
            for (GameClientThread c : clients) {
                if (c.authenticated) {
                    return c.getUsername().equals(user);
                }
            }

            return false;
        }
    }
}
