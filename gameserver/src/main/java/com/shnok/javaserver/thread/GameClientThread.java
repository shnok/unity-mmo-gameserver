package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.LoginFailPacket;
import com.shnok.javaserver.dto.external.serverpackets.RemoveObjectPacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.CharSelectInfoPackage;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.enums.network.LoginFailReason;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.network.SessionKey;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.security.BlowFishKeygen;
import com.shnok.javaserver.security.GameCrypt;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.service.GameServerController;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.service.factory.PlayerFactoryService;
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
import java.util.List;

import static com.shnok.javaserver.config.Configuration.server;

@Getter
@Setter
@Log4j2
public class GameClientThread extends Thread {
    private GameClientState gameClientState;
    private SessionKey sessionId;
    private int charSlot = -1;
    private final Socket connection;
    private final String connectionIp;
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
    private List<CharSelectInfoPackage> charSelection = null;
    private int connectionTimeoutMs;
    private boolean printCryptography;
    private boolean printPacketsIn;
    private boolean printPacketsOut;

    public GameClientThread(Socket con) {
        connection = con;
        connectionIp = con.getInetAddress().getHostAddress();
        gameCrypt = new GameCrypt();
        connectionTimeoutMs = server.serverConnectionTimeoutMs();
        printCryptography = server.printCryptography();
        printPacketsIn = server.printClientPackets();
        printPacketsOut = server.printServerPackets();

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
        if(isPrintPacketsOut()) {
            ServerPacketType packetType = ServerPacketType.fromByte(packet.getType());
            if(packetType != ServerPacketType.Ping) {
                log.debug("[CLIENT] Sent packet: {}", packetType);
            }
        }

        byte[] data = packet.getData();

        if(isCryptEnabled()) {
            data = Arrays.copyOf(packet.getData(), packet.getData().length);

            NewCrypt.appendChecksum(data);

            if(printCryptography) {
                log.debug("---> [CLIENT] Clear packet {} : {}", data.length,
                        Arrays.toString(data));
            }
            gameCrypt.encrypt(data, 0, data.length);
            if(printCryptography) {
                log.debug("---> [CLIENT] Encrypted packet {} : {}", data.length,
                        Arrays.toString(data));
            }
        } else if(printCryptography) {
            log.debug("---> [CLIENT] Clear packet {} : {}", data.length,
                    Arrays.toString(data));
        }

        try {
            synchronized (out) {
                out.write((byte)(data.length) & 0xff);
                out.write((byte)((data.length) >> 8) & 0xff);

                for (byte b : data) {
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
        log.info("Authenticating new player.");
        SystemMessagePacket systemMessagePacket = new SystemMessagePacket(SystemMessageId.S1_ONLINE);
        systemMessagePacket.addCharName(getCurrentPlayer());

        GameServerController.getInstance().broadcast(systemMessagePacket, this);
    }

    void removeSelf() {
        if (getGameClientState() == GameClientState.IN_GAME) {
            setGameClientState(GameClientState.AUTHED);

            if(!clientReady) {
                return;
            }

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

            /* broadcast log off message to server */
            SystemMessagePacket systemMessagePacket = new SystemMessagePacket(SystemMessageId.S1_OFFLINE);
            systemMessagePacket.addCharName(getCurrentPlayer());

            GameServerController.getInstance().broadcast(systemMessagePacket, this);
        }

        /* stop watch dog */
        if(watchDog != null) {
            watchDog.stop();
        }

        if(currentPlayer != null) {
            currentPlayer.destroy();
        }

        GameServerController.getInstance().removeClient(this);
        this.interrupt();
    }

    public CharSelectInfoPackage getCharSelection(int charSlot) {
        if ((charSelection == null) || (charSlot < 0) || (charSlot >= charSelection.size())) {
            return null;
        }
        return charSelection.get(charSlot);
    }

    public int getObjectIdForSlot(int charSlot) {
        CharSelectInfoPackage selectInfoPackage = getCharSelection(charSlot);
        if(selectInfoPackage == null) {
            return -1;
        }

        return selectInfoPackage.getObjectId();
    }

    public PlayerInstance loadCharFromDisk(int charSlot) {
        final int objId = getObjectIdForSlot(charSlot);
        if (objId < 0) {
            return null;
        }

        PlayerInstance character = WorldManagerService.getInstance().getPlayer(objId);
        if (character != null) {
            // exploit prevention, should not happens in normal way
            log.error("Attempt of float login {}, account {}!", character, getAccountName());
            if (character.getGameClient() != null) {
                character.getGameClient().disconnect();
            } else {
                character.destroy();
            }
            return null;
        }

        character = PlayerFactoryService.getInstance().getPlayerInstanceById(objId);
        if (character != null) {
           // character.setRunning();
          //  character.standUp();
            character.setOnlineStatus(false, true);
        } else {
            log.error("Could not restore in slot {}!", charSlot);
        }

        return character;
    }

    public void close(LoginFailReason failReason) {
        sendPacket(new LoginFailPacket(failReason));
        disconnect();
    }
}
