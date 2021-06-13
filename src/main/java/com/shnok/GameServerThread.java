package com.shnok;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GameServerThread extends Thread {
    private final Socket _connection;
    private String _connectionIp;
    private InputStream _in;
    private OutputStream _out;

    public GameServerThread(Socket con) {
        _connection = con;
        _connectionIp = con.getInetAddress().getHostAddress();
        try {
            _in = _connection.getInputStream();
            _out = new BufferedOutputStream(_connection.getOutputStream());
            System.out.println("New gameserverthread from " + _connectionIp);

        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    @Override
    public void run() {
        int packetType = 0;
        int packetLength = 0;

        try {

            for (;;) {
                packetType = _in.read();
                packetLength = _in.read();
                System.out.println("Received packet type: "+ Integer.toHexString(packetType) +
                        " length: " + packetLength + " bytes");

                if (packetType == -1 || _connection.isClosed()) {
                    System.out.println("LoginServerThread: Login terminated the connection.");
                    break;
                }

                byte[] data = new byte[packetLength - 2];
                int receivedBytes = 0;
                int newBytes = 0;

                while ((newBytes != -1) && (receivedBytes < (packetLength - 2))) {
                    newBytes = _in.read(data, 0, packetLength - 2);
                    receivedBytes = receivedBytes + newBytes;
                }

                switch (packetType) {
                    case 0x00:
                        onReceiveEcho();
                        break;
                    case 0x01:
                        onReceiveString(data);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("User " + _connectionIp +" disconnected");
            Server.getGameServerListener().removeGameServer(this);
        }
    }

    private void sendPacket() {
        //byte[] data = sl.getContent();
        //int len = data.length + 2;

        System.out.println("Sending packet");
        try {
            synchronized (_out) {
                _out.write(0x00);
                _out.write(0x02);
                // _out.write(data);
                _out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onReceiveEcho() {
        sendPacket();
    }

    private void onReceiveString(byte[] data) {
        String value = new String(data);
        System.out.println("Received string: " + value);
    }



}
