package com.shnok;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServerThread extends Thread {
    ServerSocket servSock;
    int threadNumber;

    /** Construct a Handler. */
    public GameServerThread(ServerSocket s, int i) {
        servSock = s;
        threadNumber = i;
        setName("Thread " + threadNumber);
    }

    public void run() {
        /* Wait for a connection. Synchronized on the ServerSocket
         * while calling its accept() method.
         */
        while (true) {
            try {
                System.out.println( getName() + " waiting");

                Socket clientSocket;
                // Wait here for the next connection.
                synchronized(servSock) {
                    clientSocket = servSock.accept();
                }

                System.out.println( "Request");

                System.out.println(getName() + " starting, IP=" + clientSocket.getInetAddress());
                BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //PrintStream os = new PrintStream(clientSocket.getOutputStream(), true);
                String line;

                while ((line = is.readLine()) != null) {
                    System.out.println(line);
                    /*os.print(line + "\r\n");
                    os.flush();*/
                }

                System.out.println(getName() + " ENDED ");
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println(getName() + ": IO Error on socket " + ex);
                return;
            }
        }
    }
}
