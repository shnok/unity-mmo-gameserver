package com.shnok.javaserver.thread;

import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.util.HexUtils;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.shnok.javaserver.config.Configuration.hexId;
import static com.shnok.javaserver.config.Configuration.server;

public class LoginServerThread extends Thread {
    private final String hostname;
    private final int port;
    private final int gamePort;
    private Socket loginSocket;
    private OutputStream out;

    /**
     * The BlowFish engine used to encrypt packets<br>
     * It is first initialized with a unified key:<br>
     * "_;v.]05-31!|+-%xT!^[$\00"<br>
     * <br>
     * and then after handshake, with a new key sent by<br>
     * login server during the handshake. This new key is stored<br>
     * in blowfishKey
     */
    private NewCrypt _blowfish;
    private final byte[] hexID;
    private final boolean acceptAlternate;
    private final int requestID;
    private int maxPlayer;
   // private final List<WaitingClient> waitingClients;
    private final Map<String, GameClientThread> accountsInGameServer = new ConcurrentHashMap<>();
    private int _status;
    private String _serverName;
    private final List<String> subnets;
    private final List<String> hosts;

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
}
