package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.internal.loginserver.InitLSPacket;
import com.shnok.javaserver.enums.packettypes.GameServerPacketType;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.security.Rnd;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.util.HexUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.shnok.javaserver.config.Configuration.hexId;
import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
@Getter
@Setter
public class LoginServerThread extends Thread {
    private final String hostname;
    private final int port;
    private final int gamePort;
    private Socket loginSocket;
    private OutputStream out;
    private InputStream in;

    /**
     * The BlowFish engine used to encrypt packets<br>
     * It is first initialized with a unified key:<br>
     * "_;v.]05-31!|+-%xT!^[$\00"<br>
     * <br>
     * and then after handshake, with a new key sent by<br>
     * login server during the handshake. This new key is stored<br>
     * in blowfishKey
     */
    private NewCrypt blowfish;
    private final byte[] hexID;
    private final boolean acceptAlternate;
    private final int requestID;
    private int maxPlayer;
   // private final List<WaitingClient> waitingClients;
    private final Map<String, GameClientThread> accountsInGameServer = new ConcurrentHashMap<>();
    private int status;
    private String serverName;
    private final List<String> subnets;
    private final List<String> hosts;
    private byte[] blowfishKey;

    private static LoginServerThread instance;
    public static LoginServerThread getInstance() {
        if (instance == null) {
            instance = new LoginServerThread();
        }

        return instance;
    }

    protected LoginServerThread() {
        super("LoginServerThread");
        port = server.loginServerPort();
        gamePort = server.gameserverPort();
        hostname = server.loginServerHost();
        if (hexId.getHexID() == null) {
            hexID = HexUtils.generateHex(16);
            requestID = server.requestServerId();
            hexId.setProperty("ServerID", String.valueOf(requestID));
        } else {
            hexID = hexId.getHexID().toByteArray();
            requestID = hexId.getServerID();
        }
        acceptAlternate = server.acceptAlternateId();
        subnets = Collections.singletonList("0.0.0.0/0");
        hosts = Collections.singletonList("127.0.0.1");
        //waitingClients = new CopyOnWriteArrayList<>();
        maxPlayer = server.maxOnlineUser();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                tryConnectToLoginServer();
                initBlowfish();

                while (!isInterrupted()) {
                    if(!readLoginServerPackets()) {
                        break;
                    }
                }
            } catch (UnknownHostException e) {
                log.warn("Unknown host!", e);
            } catch (SocketException e) {
                log.warn("LoginServer not avaible, trying to reconnect...");
            } catch (IOException e) {
                log.warn("Disconnected from Login, Trying to reconnect!", e);
            } finally {
                try {
                    loginSocket.close();
                    if (isInterrupted()) {
                        return;
                    }
                } catch (Exception ignored) {
                }
            }

            try {
                Thread.sleep(5000); // 5 seconds tempo.
            } catch (InterruptedException e) {
                return; // never swallow an interrupt!
            }
        }
    }

    private void tryConnectToLoginServer() throws IOException {
        // Connection
        log.info("Connecting to login server on {}:{}", hostname, port);
        loginSocket = new Socket(hostname, port);

        in = loginSocket.getInputStream();
        out = new BufferedOutputStream(loginSocket.getOutputStream());
    }

    private void initBlowfish() {
        // init Blowfish
        blowfishKey = HexUtils.generateHex(40);
        // Protect the new blowfish key what cannot begin with zero
        if (blowfishKey[0] == 0) {
            blowfishKey[0] = (byte) Rnd.get(32, 64);
        }

        blowfish = new NewCrypt("_;v.]05-31!|+-%xT!^[$\00");
    }

    private boolean readLoginServerPackets() throws IOException {
        int lengthLo = in.read();
        int lengthHi = in.read();
        int length = (lengthHi * 256) + lengthLo;

        if ((lengthHi < 0) || loginSocket.isClosed()) {
            log.warn("Loginserver terminated the connection!");
            return false;
        }

        byte[] data = new byte[length];

        int receivedBytes = 0;
        int newBytes = 0;
        while ((newBytes != -1) && (receivedBytes < (length))) {
            newBytes = in.read(data, 0, length);
            receivedBytes = receivedBytes + newBytes;
        }

        handlePacket(data);

        return true;
    }

    void handlePacket(byte[] data) {
        ThreadPoolManagerService.getInstance().handlePacket(new LoginServerPacketHandler(this, data));
    }

    public boolean sendPacket(SendablePacket packet) {
        if(server.printServerPackets()) {
            GameServerPacketType packetType = GameServerPacketType.fromByte(packet.getType());
            log.debug("Sent packet: {}", packetType);
        }

        log.debug("---> Clear packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));
        blowfish.crypt(packet.getData(), 0, packet.getData().length);
        log.debug("---> Encrypted packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));

        try {
            synchronized (out) {
                out.write(packet.getLength() & 0xff);
                out.write((packet.getLength() >> 8) & 0xff);
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

    public void disconnect() {
        try {
            loginSocket.close();
        } catch (IOException e) {
            log.error("Error while closing connection.", e);
        }
    }
}
