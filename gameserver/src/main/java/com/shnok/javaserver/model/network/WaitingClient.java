package com.shnok.javaserver.model.network;

import com.shnok.javaserver.thread.GameClientThread;

public class WaitingClient {
    public String account;
    public GameClientThread gameClient;
    public SessionKey session;

    /**
     * Instantiates a new waiting client.
     * @param acc the acc
     * @param client the client
     * @param key the key
     */
    public WaitingClient(String acc, GameClientThread client, SessionKey key) {
        account = acc;
        gameClient = client;
        session = key;
    }
}
