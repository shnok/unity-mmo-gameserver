package com.shnok;

import com.shnok.clientpackets.*;
import com.shnok.model.GameObject;
import com.shnok.model.entities.Entity;
import com.shnok.model.entities.NpcInstance;
import com.shnok.model.entities.PlayerInstance;
import com.shnok.model.Point3D;
import com.shnok.serverpackets.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ClientPacketHandler extends Thread {
    private final GameClient _client;
    private byte[] _data;

    public ClientPacketHandler(GameClient client, byte[] data) {
        _client = client;
        _data = data;
    }
    @Override
    public void run(){
        handle();
    }

    public void handle() {
        byte type = _data[0];

        switch (type) {
            case 0x00:
                onReceiveEcho();
                break;
            case 0x01:
                onReceiveAuth(_data);
                break;
            case 0x02:
                onReceiveMessage(_data);
                break;
            case 0x03:
                onRequestCharacterMove(_data);
                break;
            case 0x04:
                onRequestLoadWorld();
                break;
            case 0x05:
                onRequestCharacterRotate(_data);
                break;
            case 0x06:
                onRequestCharacterAnimation(_data);
                break;
            case 0x07:
                onRequestAttack(_data);
                break;
        }
    }

    private void onReceiveEcho() {
        _client.sendPacket(new PingPacket());
        _client.setLastEcho(System.currentTimeMillis());

        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(System.currentTimeMillis() - _client.getLastEcho() > 1500) {
                    _client.removeSelf();
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

    private void onRequestCharacterMove(byte[] data) {
        RequestCharacterMove packet = new RequestCharacterMove(data);
        Point3D newPos = packet.getPosition();

        PlayerInstance currentPlayer = _client.getCurrentPlayer();
        currentPlayer.setPosition(newPos);

        ObjectPosition objectPosition = new ObjectPosition(currentPlayer.getId(), newPos);
        Server.getInstance().broadcast(objectPosition, _client);
    }

    private void onRequestLoadWorld() {
        for (Map.Entry<Integer, PlayerInstance> pair : World.getInstance().getAllPlayers().entrySet()) {
            System.out.println(pair.getValue());
            _client.sendPacket(new PlayerInfo(pair.getValue()));
        }

        for (Map.Entry<Integer, NpcInstance> pair : World.getInstance().getAllNpcs().entrySet()) {
            System.out.println(pair.getValue());
            _client.sendPacket(new NpcInfo(pair.getValue()));
        }
    }

    private void onRequestCharacterRotate(byte[] data) {
        RequestCharacterRotate packet = new RequestCharacterRotate(data);
        ObjectRotation objectRotation = new ObjectRotation(_client.getCurrentPlayer().getId(), packet.getAngle());
        Server.getInstance().broadcast(objectRotation, _client);
    }

    private void onRequestCharacterAnimation(byte[] data) {
        RequestCharacterAnimation packet = new RequestCharacterAnimation(data);
        ObjectAnimation objectAnimation = new ObjectAnimation(_client.getCurrentPlayer().getId(), packet.getAnimId(), packet.getValue());
        Server.getInstance().broadcast(objectAnimation, _client);
    }

    private void onRequestAttack(byte[] data) {
        RequestAttack packet = new RequestAttack(data);

        Entity entity = World.getInstance().getEntity(packet.getTargetId());
        entity.inflictDamage(1);

        ApplyDamage applyDamage = new ApplyDamage(_client.getCurrentPlayer().getId(), packet.getTargetId(), packet.getAttackType(), 1);
        System.out.println(packet.getTargetId());
        Server.getInstance().broadcastAll(applyDamage);
    }
}
