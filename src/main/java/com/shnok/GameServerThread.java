package com.shnok;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class GameServerThread extends Thread {
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
    }

    @Override
    public void run() {
        startReadingPackets();
    }

    private void startReadingPackets() {
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

                handlePacket((byte) packetType, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("User " + _connectionIp +" disconnected");
            removeSelf();
        }
    }

    abstract void removeSelf();
    abstract void handlePacket(byte type, byte[] data);

    public void sendPacket(byte[] packet) {
        System.out.println("Sending packet");
        try {
            synchronized (_out) {
                for(int i = 0; i < packet.length; i++) {
                    _out.write(packet[i]);
                }
                _out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
