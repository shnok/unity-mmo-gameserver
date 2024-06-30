package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.RemoveObjectPacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.enums.GameClientState;
import com.shnok.javaserver.enums.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.network.SessionKey;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.security.BlowFishKeygen;
import com.shnok.javaserver.security.GameCrypt;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.service.GameServerController;
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
import java.util.Arrays;

import static com.shnok.javaserver.config.Configuration.server;

@Getter
@Setter
@Log4j2
public class GameClientThread extends Thread {
    private GameClientState gameClientState;
    private SessionKey sessionId;
    private int _charSlot = -1;
    private final Socket connection;
    private final String connectionIp;
    public boolean authenticated;
    private InputStream in;
    private OutputStream out;
    private String accountName;
    private PlayerInstance currentPlayer;
    private boolean clientReady = false;
    private long lastEcho;
    private Timer watchDog;
    private GameCrypt gameCrypt;
    private boolean protocolOk;
    private boolean cryptEnabled;


    public GameClientThread(Socket con) {
        connection = con;
        connectionIp = con.getInetAddress().getHostAddress();
        gameCrypt = new GameCrypt();

        try {
            in = connection.getInputStream();
            out = new BufferedOutputStream(connection.getOutputStream());
            log.debug("New connection: {}", connectionIp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] enableCrypt() {
        byte[] key = BlowFishKeygen.getRandomKey();
        gameCrypt.setKey(key);
        return key;
    }

    @Override
    public void run() {
        startReadingPackets();
    }

    private void startReadingPackets() {
        int lengthHi;
        int lengthLo;
        int length;

        try {
            for (; ; ) {
                lengthLo = in.read();
                lengthHi = in.read();
                length = (lengthHi * 256) + lengthLo;

                if ((lengthHi < 0) || connection.isClosed()) {
                    log.warn("Gameserver terminated the connection!");
                    break;
                }

                byte[] data = new byte[length];

                int receivedBytes = 0;
                int newBytes = 0;
                while ((newBytes != -1) && (receivedBytes < (length))) {
                    newBytes = in.read(data, 0, length);
                    receivedBytes = receivedBytes + newBytes;
                }

                handlePacket(data);
            }
        } catch (Exception e) {
            log.error("Exception while reading packets.");
        } finally {
            log.info("User {} disconnected", connectionIp);
            disconnect();
        }
    }

    public void disconnect() {
        try {
            //TODO: Save user state
            LoginServerThread.getInstance().sendLogout(getAccountName());
            removeSelf();
            connection.close();
        } catch (IOException e) {
            log.error("Error while closing connection.", e);
        }
    }

    public boolean sendPacket(SendablePacket packet) {
        if(server.printServerPackets()) {
            ServerPacketType packetType = ServerPacketType.fromByte(packet.getType());
            if(packetType != ServerPacketType.Ping) {
                log.debug("[GAME] Sent packet: {}", packetType);
            }
        }

        if(isCryptEnabled()) {
            NewCrypt.appendChecksum(packet.getData());

            log.debug("---> [GAME] Clear packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));
            LoginServerThread.getInstance().getBlowfish().crypt(packet.getData(), 0, packet.getData().length);
            log.debug("---> [GAME] Encrypted packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));
        } else {
            log.debug("---> [GAME] Clear packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));
        }

        try {
            synchronized (out) {
                out.write((byte)(packet.getData().length) & 0xff);
                out.write((byte)((packet.getData().length) >> 8) & 0xff);

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
        GameServerController.getInstance().broadcast(
                new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_IN, accountName), this);
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
            GameServerController.getInstance().broadcast(
                    new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_OFF, accountName), this);
        }

        GameServerController.getInstance().removeClient(this);
        this.interrupt();
    }
}
