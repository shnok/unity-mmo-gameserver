package com.shnok;

import com.shnok.clientpackets.AuthRequest;
import com.shnok.clientpackets.RequestSendMessage;
import com.shnok.serverpackets.AuthResponse;
import com.shnok.serverpackets.MessagePacket;
import com.shnok.serverpackets.PingPacket;
import com.shnok.serverpackets.SystemMessagePacket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ClientPacketHandler {
    private final GameClient _client;
    private long _lastEcho;

    public ClientPacketHandler(GameClient client) {
        _client = client;
    }

    public void handle(byte[] data) {
        byte type = data[0];

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
        AuthRequest packet = new AuthRequest(data);
        String username = packet.getUsername();

        AuthResponse authResponse;
        if(Server.getInstance().userExists(username)) {
            authResponse = new AuthResponse(AuthResponse.AuthResponseType.ALREADY_CONNECTED);
        } else if(username.length() <= 0 || username.length() > 16) {
            authResponse = new AuthResponse(AuthResponse.AuthResponseType.INVALID_USERNAME);
        } else {
            authResponse = new AuthResponse(AuthResponse.AuthResponseType.ALLOW);
            _client.authenticated = true;
            _client.setUsername(username);
        }

        _client.sendPacket(authResponse);


        if(_client.authenticated) {
            _client.authenticate();
        }
    }

    private void onReceiveMessage(byte[] data) {
        RequestSendMessage packet = new RequestSendMessage(data);
        String message = packet.getMessage();

        MessagePacket messagePacket = new MessagePacket(_client.getUsername(), message);
        Server.getInstance().broadcast(messagePacket);
    }
}
