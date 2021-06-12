package com.shnok;

import java.io.IOException;
import java.net.ServerSocket;

public class GameServerListener {
    public GameServerListener(int port, int numThreads) {
        ServerSocket servSock;

        try {
            servSock = new ServerSocket(port);

        } catch(IOException e) {
            /* Crash the server if IO fails. Something bad has happened */
            throw new RuntimeException("Could not create ServerSocket ", e);
        }

        // Create a series of threads and start them.
        for (int i=0; i<numThreads; i++) {
            new GameServerThread(servSock, i).start();
        }

    }
}
