package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.clientpackets.*;
import com.shnok.javaserver.dto.serverpackets.*;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entities.Entity;
import com.shnok.javaserver.model.entities.NpcInstance;
import com.shnok.javaserver.model.entities.PlayerInstance;
import com.shnok.javaserver.service.GameServerListenerService;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

@Log4j2
public class ClientPacketHandlerThread extends Thread {
    private final GameClientThread client;
    private final byte[] data;

    public ClientPacketHandlerThread(GameClientThread client, byte[] data) {
        this.client = client;
        this.data = data;
    }

    @Override
    public void run() {
        handle();
    }

    public void handle() {
        byte type = data[0];

        log.debug("Received packet: {}", type);
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
            case 0x03:
                onRequestCharacterMove(data);
                break;
            case 0x04:
                onRequestLoadWorld();
                break;
            case 0x05:
                onRequestCharacterRotate(data);
                break;
            case 0x06:
                onRequestCharacterAnimation(data);
                break;
            case 0x07:
                onRequestAttack(data);
                break;
        }
    }

    private void onReceiveEcho() {
        client.sendPacket(new PingPacket());
        client.setLastEcho(System.currentTimeMillis());

        Timer timer = new Timer(GameServerListenerService.TIMEOUT_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (System.currentTimeMillis() - client.getLastEcho() >= GameServerListenerService.TIMEOUT_MS) {
                    log.info("User connection timeout.");
                    client.removeSelf();
                    client.disconnect();
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
        if (client.getServerService().userExists(username)) {
            authResponse = new AuthResponse(AuthResponse.AuthResponseType.ALREADY_CONNECTED);
        } else if (username.length() <= 0 || username.length() > 16) {
            authResponse = new AuthResponse(AuthResponse.AuthResponseType.INVALID_USERNAME);
        } else {
            authResponse = new AuthResponse(AuthResponse.AuthResponseType.ALLOW);
            client.authenticated = true;
            client.setUsername(username);
        }

        client.sendPacket(authResponse);

        if (client.authenticated) {
            client.authenticate();
        }
    }

    private void onReceiveMessage(byte[] data) {
        RequestSendMessage packet = new RequestSendMessage(data);
        String message = packet.getMessage();

        MessagePacket messagePacket = new MessagePacket(client.getUsername(), message);
        client.getServerService().broadcast(messagePacket);
    }

    private void onRequestCharacterMove(byte[] data) {
        RequestCharacterMove packet = new RequestCharacterMove(data);
        Point3D newPos = packet.getPosition();

        PlayerInstance currentPlayer = client.getCurrentPlayer();
        currentPlayer.setPosition(newPos);

        ObjectPosition objectPosition = new ObjectPosition(currentPlayer.getId(), newPos);
        client.getServerService().broadcast(objectPosition, client);
    }

    private void onRequestLoadWorld() {
        for (Map.Entry<Integer, PlayerInstance> pair : client.getWorldManagerService().getAllPlayers().entrySet()) {
            client.sendPacket(new PlayerInfo(pair.getValue()));
        }

        for (Map.Entry<Integer, NpcInstance> pair : client.getWorldManagerService().getAllNpcs().entrySet()) {
            client.sendPacket(new NpcInfo(pair.getValue()));
        }
    }

    private void onRequestCharacterRotate(byte[] data) {
        RequestCharacterRotate packet = new RequestCharacterRotate(data);
        ObjectRotation objectRotation = new ObjectRotation(client.getCurrentPlayer().getId(), packet.getAngle());
        client.getServerService().broadcast(objectRotation, client);
    }

    private void onRequestCharacterAnimation(byte[] data) {
        RequestCharacterAnimation packet = new RequestCharacterAnimation(data);
        ObjectAnimation objectAnimation = new ObjectAnimation(client.getCurrentPlayer().getId(), packet.getAnimId(), packet.getValue());
        client.getServerService().broadcast(objectAnimation, client);
    }

    private void onRequestAttack(byte[] data) {
        RequestAttack packet = new RequestAttack(data);

        Entity entity = client.getWorldManagerService().getEntity(packet.getTargetId());
        entity.inflictDamage(1);

        ApplyDamage applyDamage = new ApplyDamage(client.getCurrentPlayer().getId(), packet.getTargetId(), packet.getAttackType(), 1);
        client.getServerService().broadcastAll(applyDamage);
    }
}
