package com.shnok.javaserver.thread;

import com.shnok.javaserver.config.Configuration;
import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.ServerClosePacket;
import com.shnok.javaserver.dto.internal.gameserver.PlayerAuthRequestPacket;
import com.shnok.javaserver.dto.internal.gameserver.PlayerLogoutPacket;
import com.shnok.javaserver.dto.internal.gameserver.ServerStatusPacket;
import com.shnok.javaserver.enums.network.packettypes.internal.GameServerPacketType;
import com.shnok.javaserver.model.network.SessionKey;
import com.shnok.javaserver.model.network.WaitingClient;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.security.Rnd;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.util.HexUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.shnok.javaserver.config.Configuration.hexId;
import static com.shnok.javaserver.config.Configuration.server;
import static com.shnok.javaserver.config.HexIdConfig.HEXID_KEY;
import static com.shnok.javaserver.config.HexIdConfig.SERVERID_KEY;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

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
    private final Map<String, GameClientThread> accountsInGameServer = new ConcurrentHashMap<>();
    private int status;
    private String serverName;
    private final List<String> subnets;
    private final List<String> hosts;
    private byte[] blowfishKey;
    private final List<WaitingClient> waitingClients;
    private boolean printCryptography;
    private boolean printPacketsIn;
    private boolean printPacketsOut;

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
        printCryptography = server.printCryptography();
        printPacketsIn = server.printClientPackets();
        printPacketsOut = server.printServerPackets();

        if (hexId.getHexID() == null) {
            hexID = HexUtils.generateHex(16);
            requestID = server.requestServerId();
            hexId.setProperty("ServerID", String.valueOf(requestID));
        } else {
            hexID = HexUtils.toUnsignedByteArray(hexId.getHexID());
            requestID = hexId.getServerID();
        }

        acceptAlternate = server.acceptAlternateId();
        subnets = Collections.singletonList("0.0.0.0/0");
        hosts = Collections.singletonList("127.0.0.1");
        //waitingClients = new CopyOnWriteArrayList<>();
        maxPlayer = server.maxOnlineUser();
        waitingClients = new ArrayList<>();
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
                disconnect();
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
        if(isPrintPacketsOut()) {
            GameServerPacketType packetType = GameServerPacketType.fromByte(packet.getType());
            log.debug("[LOGIN] Sent packet: {}", packetType);
        }

        NewCrypt.appendChecksum(packet.getData());

        if(isPrintCryptography()) {
            log.debug("---> [LOGIN] Clear packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));
        }
        blowfish.crypt(packet.getData(), 0, packet.getData().length);
        if(isPrintCryptography()) {
            log.debug("---> [LOGIN] Encrypted packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));
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

    public void disconnect() {
        try {
            loginSocket.close();
        } catch (Exception e) {
        }
    }

    /**
     * Save hexadecimal ID of the server in the L2Properties file.
     * @param serverId the ID of the server whose hexId to save
     * @param newHexId the hexadecimal ID to store
     */
    public void saveHexId(int serverId, String newHexId) {
        Path hexIdFilePath = Configuration.getCustomOrDefaultPath(hexId.FILENAME);
        hexId.setProperty(SERVERID_KEY, String.valueOf(serverId));
        hexId.setProperty(HEXID_KEY, newHexId);

        try {
            Files.createDirectories(hexIdFilePath.getParent());
            try (OutputStream out = Files.newOutputStream(hexIdFilePath, CREATE, TRUNCATE_EXISTING)) {
                hexId.store(out, "the hexID to auth into login");
                log.info("Saved {}.", hexIdFilePath);
            }
        } catch (Exception ex) {
            log.warn("Failed to save {}.", hexIdFilePath, ex);
        }
    }

    /**
     * Send logout for the given account.
     * @param account the account
     */
    public void sendLogout(String account) {
        if (account == null) {
            return;
        }

        PlayerLogoutPacket pl = new PlayerLogoutPacket(account);
        sendPacket(pl);

        removedLoggedAccount(account);
    }

    /**
     * Adds the game server login.
     * @param account the account
     * @param client the client
     * @return {@code true} if account was not already logged in, {@code false} otherwise
     */
    public boolean addLoggedAccount(String account, GameClientThread client) {
        boolean wasNotLoggedIn = accountsInGameServer.putIfAbsent(account, client) == null;

        calculateNewStatus();

        return wasNotLoggedIn;
    }

    public void removedLoggedAccount(String account) {
        accountsInGameServer.remove(account);
        calculateNewStatus();
    }

    public void calculateNewStatus() {
        if(status == ServerStatusPacket.STATUS_DOWN || status == ServerStatusPacket.STATUS_GM_ONLY) {
            return;
        }

        int onlineCount = accountsInGameServer.size();
        float chargePercent = (float)onlineCount / (float)maxPlayer * 100;

        if(chargePercent <= 25) {
            setStatus(ServerStatusPacket.STATUS_LIGHT);
        } else if(chargePercent < 75) {
            setStatus(ServerStatusPacket.STATUS_NORMAL);
        } else if(chargePercent < 100) {
            setStatus(ServerStatusPacket.STATUS_HEAVY);
        } else {
            setStatus(ServerStatusPacket.STATUS_FULL);
        }
    }

    public void setStatus(int value) {
        if(status != value) {
            status = value;
            log.info("Server {}[{}] status changed to {}.", getServerName(),
                    getId(), getStatusName());

            ServerStatusPacket packet = new ServerStatusPacket();
            packet.addAttribute(ServerStatusPacket.SERVER_LIST_STATUS, value);
            sendPacket(packet);
        }
    }

    public String getStatusName() {
        switch (status) {
            case 0: return "Light";
            case 1: return "Normal";
            case 2: return "Heavy";
            case 3: return "Full";
            case 4: return "Down";
            case 5: return "GM Only";
            default: return "Unknown";
        }
    }

    /**
     * Adds the waiting client and send request.
     * @param account the account
     * @param client the game client
     * @param key the session key
     */
    public void addWaitingClientAndSendRequest(String account, GameClientThread client, SessionKey key) {
        WaitingClient wc = new WaitingClient(account, client, key);
        synchronized (waitingClients) {
            waitingClients.add(wc);
        }

        PlayerAuthRequestPacket par = new PlayerAuthRequestPacket(account, key);
        sendPacket(par);
    }

    /**
     * Removes the waiting client.
     * @param client the client
     */
    public void removeWaitingClient(GameClientThread client) {
        WaitingClient toRemove = null;
        synchronized (waitingClients) {
            for (WaitingClient c : waitingClients) {
                if (c.gameClient == client) {
                    toRemove = c;
                }
            }
            if (toRemove != null) {
                waitingClients.remove(toRemove);
            }
        }
    }

    /**
     * Kick player for the given account.
     * @param account the account
     */
    public void kickPlayer(String account) {
        GameClientThread client = accountsInGameServer.get(account);
        if (client != null) {
            log.warn("Kicked by login server: {}", client.getAccountName());
            client.sendPacket(new ServerClosePacket());
            client.disconnect();
        }
    }
}
