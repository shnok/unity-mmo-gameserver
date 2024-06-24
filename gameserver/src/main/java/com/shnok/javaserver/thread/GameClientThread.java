package com.shnok.javaserver.thread;

import com.shnok.javaserver.config.ServerConfig;
import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.dto.serverpackets.RemoveObjectPacket;
import com.shnok.javaserver.dto.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.shnok.javaserver.config.Configuration.serverConfig;

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
    private Timer watchDog;

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

    public boolean sendPacket(ServerPacket packet) {
        if(serverConfig.printServerPackets()) {
            ServerPacketType packetType = ServerPacketType.fromByte(packet.getType());
            if(packetType != ServerPacketType.Ping) {
                log.debug("Sent packet: {}", packetType);
            }
        }

        try {
            synchronized (out) {
                for (byte b : packet.getData()) {
                    out.write(b & 0xFF);
                }
                out.flush();
            }

            return true;
        } catch (IOException e) {
            log.warn("Trying to send packet to a closed game client.");
        }

        return false;
    }

    void handlePacket(byte[] data) {
        ThreadPoolManagerService.getInstance().handlePacket(new ClientPacketHandlerThread(this, data));
    }

    public void setLastEcho(long lastEcho, Timer watchDog) {
        if(this.watchDog != null) {
            watchDog.stop();
        }

        this.lastEcho = lastEcho;
        this.watchDog = watchDog;
    }

    void authenticate() {
        log.debug("Authenticating new player.");
        ServerService.getInstance().broadcast(
                new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_IN, username), this);
    }

    void removeSelf() {
        if (authenticated) {
            authenticated = false;

            if(!clientReady) {
                return;
            }

            /* remove player from world player list */
            WorldManagerService.getInstance().removePlayer(currentPlayer);

            /* remove player from world object list */
//            WorldManagerService.getInstance().removeObject(currentPlayer);

            /* tell to knownplayers to remove object */
            /* could be redundant... */
            currentPlayer.broadcastPacket(new RemoveObjectPacket(currentPlayer.getId()));

            /* tell knownlist to forget player */
//            currentPlayer.getKnownList().getKnownObjects().values().forEach(
//                    (object) ->  object.getKnownList().removeKnownObject(currentPlayer));

            /* tell player to forget every object */
            currentPlayer.getKnownList().getKnownObjects().forEach((integer, gameObject) -> {
                currentPlayer.getKnownList().removeKnownObject(gameObject);
            });

//            WorldManagerService.getInstance().getVisibleObjects(getActiveObject())

            currentPlayer.setVisible(false);

            /* remove player from region */
            currentPlayer.getPosition().getWorldRegion().removeVisibleObject(currentPlayer);

            /* stop watch dog */
            if(watchDog != null) {
                watchDog.stop();
            }

            /* broadcast log off message to server */
            ServerService.getInstance().broadcast(
                    new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_OFF, username), this);
        }

        ServerService.getInstance().removeClient(this);
        this.interrupt();
    }
}
