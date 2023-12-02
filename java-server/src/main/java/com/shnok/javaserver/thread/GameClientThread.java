package com.shnok.javaserver.thread;

import com.shnok.javaserver.service.DatabaseMockupService;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.model.entities.PlayerInstance;
import com.shnok.javaserver.model.status.PlayerStatus;
import com.shnok.javaserver.dto.serverpackets.PlayerInfo;
import com.shnok.javaserver.dto.serverpackets.RemoveObject;
import com.shnok.javaserver.dto.serverpackets.SystemMessage;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Getter
@Setter
@Log4j2
public class GameClientThread extends Thread {
    private ServerService serverService;
    private WorldManagerService worldManagerService;
    private ThreadPoolManagerService threadPoolManagerService;
    private DatabaseMockupService databaseMockupService;
    private final Socket connection;
    private final String connectionIp;
    public boolean authenticated;
    private InputStream in;
    private OutputStream out;
    private String username;
    private PlayerInstance player;
    private long lastEcho;

    public GameClientThread(
            ServerService serverService,
            WorldManagerService worldManagerService,
            ThreadPoolManagerService threadPoolManagerService,
            DatabaseMockupService databaseMockupService,
            Socket con) {
        connection = con;
        connectionIp = con.getInetAddress().getHostAddress();
        this.serverService = serverService;
        this.worldManagerService = worldManagerService;
        this.threadPoolManagerService = threadPoolManagerService;
        this.databaseMockupService = databaseMockupService;

        try {
            in = connection.getInputStream();
            out = new BufferedOutputStream(connection.getOutputStream());
            log.debug("New connection: {}" + connectionIp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startReadingPackets();
    }

    private void startReadingPackets() {
        int packetType;
        int packetLength;

        try {
            for (; ; ) {
                packetType = in.read();
                packetLength = in.read();

                if (packetType == -1 || connection.isClosed()) {
                    log.warn("Connection was closed.");
                    break;
                }

                byte[] data = new byte[packetLength];
                data[0] = (byte) packetType;
                data[1] = (byte) packetLength;

                int receivedBytes = 0;
                int newBytes = 0;

                while ((newBytes != -1) && (receivedBytes < (packetLength - 2))) {
                    newBytes = in.read(data, 2, packetLength - 2);
                    receivedBytes = receivedBytes + newBytes;
                }

                handlePacket(data);
            }
        } catch (Exception e) {
            log.error("Exception while reading packets", e);
        } finally {
            log.info("User {} disconnected", connectionIp);
            removeSelf();
            disconnect();
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (IOException e) {
            log.error("Error while closing connection.", e);
        }
    }

    public void sendPacket(ServerPacket packet) {
        log.debug("Sent packet: {}", packet.getType());
        try {
            synchronized (out) {
                for (byte b : packet.getData()) {
                    out.write(b & 0xFF);
                }
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getLastEcho() {
        return lastEcho;
    }

    public void setLastEcho(long lastEcho) {
        this.lastEcho = lastEcho;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PlayerInstance getCurrentPlayer() {
        return player;
    }

    void handlePacket(byte[] data) {
        threadPoolManagerService.handlePacket(new ClientPacketHandlerThread(this, data));
    }

    void authenticate() {
        log.debug("Authenticating new player.");
        serverService.broadcast(new SystemMessage(SystemMessage.MessageType.USER_LOGGED_IN, username));
        player = databaseMockupService.getPlayerData(username);
        player.setId(worldManagerService.nextID());
        worldManagerService.addPlayer(player);
        serverService.broadcast(new PlayerInfo(player), this);
    }

    void removeSelf() {
        if (authenticated) {
            authenticated = false;
            serverService.broadcast(new SystemMessage(SystemMessage.MessageType.USER_LOGGED_OFF, username), this);
            serverService.broadcast(new RemoveObject(player.getId()), this);
            worldManagerService.removePlayer(player);
        }

        serverService.removeClient(this);
        this.interrupt();
    }
}
