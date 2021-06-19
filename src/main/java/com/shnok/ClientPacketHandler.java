package com.shnok;

import com.shnok.serverpackets.AuthPacket;
import com.shnok.serverpackets.MessagePacket;
import com.shnok.serverpackets.PingPacket;
import com.shnok.serverpackets.AuthPacket.AuthReason;
import com.shnok.serverpackets.SystemMessagePacket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ClientPacketHandler {
    private GameClient _client;
    private long _lastEcho;

    public ClientPacketHandler(GameClient client) {
        _client = client;
    }

    public void handle(byte type, byte[] data) {
        if(type != 0x00) {
            System.out.println("Received: " + Arrays.toString(data));
        }

        switch (type) {
            case 0x00:
                onReceiveEcho();
                break;
            case 0x01:
                onReceiveAuth(data);
                break;
            case 0x02:
                onReceiveMessage(data);
                break;
        }
    }

    private void onReceiveEcho() {
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

    private void onReceiveAuth(byte[] data) {
        String username = new String(data);
        AuthPacket authPacket;
        if(Server.userExists(username)) {
            authPacket = new AuthPacket(AuthReason.ALREADY_CONNECTED);
        } else if(username.length() <= 0 || username.length() > 16) {
            authPacket = new AuthPacket(AuthReason.INVALID_USERNAME);
        } else {
            authPacket = new AuthPacket(AuthReason.ALLOW);
            _client.authenticated = true;
            _client.setUsername(username);
        }

        _client.sendPacket(authPacket);

        if(_client.authenticated) {
            Server.broadcast(new SystemMessagePacket(SystemMessagePacket.MessageType.USER_LOGGED_IN, username));
        }
    }

    private void onReceiveMessage(byte[] data) {
        String value = new String(data);
        MessagePacket packet = new MessagePacket(_client.getUsername(), value);

        Server.broadcast(packet);
    }
}
