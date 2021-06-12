package com.shnok;

public class Server {

    public static final int ECHOPORT = 11000;
    public static final int NUM_THREADS = 1;

    public static void main(String[] av) {
        new GameServerListener(ECHOPORT, NUM_THREADS);
    }
}
