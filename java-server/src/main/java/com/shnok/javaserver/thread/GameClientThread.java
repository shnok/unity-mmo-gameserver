package com.shnok.javaserver.thread;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.dto.serverpackets.RemoveObjectPacket;
import com.shnok.javaserver.dto.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.entity.PlayerInstance;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Getter
@Setter
@Log4j2
public class GameClientThread extends Thread {
    private final Socket connection;
    private final String connectionIp;
    public boolean authenticated;
    private InputStream in;
    private OutputStream out;
    private String username;
    private PlayerInstance currentPlayer;
    private boolean clientReady = false;
    private long lastEcho;

    public GameClientThread(Socket con) {
        connection = con;
        connectionIp = con.getInetAddress().getHostAddress();

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
            log.error("Exception while reading packets.");
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
        if(Config.PRINT_SERVER_PACKETS) {
            log.debug("Sent packet: {}", ServerPacketType.fromByte(packet.getType()));
        }
        try {
            synchronized (out) {
                for (byte b : packet.getData()) {
                    out.write(b & 0xFF);
                }
                out.flush();
            }
        } catch (IOException e) {
            log.warn("Trying to send packet to a closed game client.");
        }
    }

    public long getLastEcho() {
        return lastEcho;
    }

    public void setLastEcho(long lastEcho) {
        this.lastEcho = lastEcho;
    }

    void handlePacket(byte[] data) {
        ThreadPoolManagerService.getInstance().handlePacket(new ClientPacketHandlerThread(this, data));
    }

    void authenticate() {
        log.debug("Authenticating new player.");
        ServerService.getInstance().broadcast(
                new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_IN, username), this);
    }

    void removeSelf() {
        if (authenticated) {
            authenticated = false;

            WorldManagerService.getInstance().removePlayer(currentPlayer);
            currentPlayer.getPosition().getWorldRegion().removeVisibleObject(currentPlayer);

            ServerService.getInstance().broadcast(
                    new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_OFF, username), this);
            ServerService.getInstance().broadcast(
                    new RemoveObjectPacket(currentPlayer.getId()), this);
        }

        ServerService.getInstance().removeClient(this);
        this.interrupt();
    }
}
