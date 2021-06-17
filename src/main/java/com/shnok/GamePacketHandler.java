package com.shnok;

import com.shnok.serverpackets.PingPacket;
import com.shnok.serverpackets.StringPacket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePacketHandler {
    private GameClient _client;
    private long _lastEcho;

    public GamePacketHandler(GameClient client) {
        _client = client;
    }

    public void handle(byte type, byte[] data) {
        switch (type) {
            case 0x00:
                onReceiveEcho();
                break;
            case 0x01:
                onReceiveString(data);
                break;
        }
    }

    private void onReceiveEcho() {
        System.out.println("Ping");

        _client.sendPacket(new PingPacket());
        _lastEcho = System.currentTimeMillis();

        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(System.currentTimeMillis() - _lastEcho > 1500) {
                    _client.disconnect();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void onReceiveString(byte[] data) {
        String value = new String(data);
        StringPacket packet = new StringPacket("Hello world!");
        _client.sendPacket(packet);
        System.out.println("Received string: " + value);
    }
}
