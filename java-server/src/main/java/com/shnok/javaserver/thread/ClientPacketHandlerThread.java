package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.clientpackets.*;
import com.shnok.javaserver.dto.serverpackets.*;
import com.shnok.javaserver.enums.ClientPacketType;
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
        ClientPacketType type = ClientPacketType.fromByte(data[0]);

        log.debug("Received packet: {}", type);
        switch (type) {
            case Ping:
                onReceiveEcho();
                break;
            case AuthRequest:
                onReceiveAuth(data);
                break;
            case SendMessage:
                onReceiveMessage(data);
                break;
            case RequestMove:
                onRequestCharacterMove(data);
                break;
            case LoadWorld:
                onRequestLoadWorld();
                break;
            case RequestRotate:
                onRequestCharacterRotate(data);
                break;
            case RequestAnim:
                onRequestCharacterAnimation(data);
                break;
            case RequestAttack:
                onRequestAttack(data);
                break;
            case RequestMoveDirection:
                onRequestCharacterMoveDirection(data);
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
        AuthRequestPacket packet = new AuthRequestPacket(data);
        String username = packet.getUsername();

        AuthResponsePacket authResponsePacket;
        if (client.getServerService().userExists(username)) {
            authResponsePacket = new AuthResponsePacket(AuthResponsePacket.AuthResponseType.ALREADY_CONNECTED);
        } else if (username.length() <= 0 || username.length() > 16) {
            authResponsePacket = new AuthResponsePacket(AuthResponsePacket.AuthResponseType.INVALID_USERNAME);
        } else {
            authResponsePacket = new AuthResponsePacket(AuthResponsePacket.AuthResponseType.ALLOW);
            client.authenticated = true;
            client.setUsername(username);
        }

        client.sendPacket(authResponsePacket);

        if (client.authenticated) {
            client.authenticate();
        }
    }

    private void onReceiveMessage(byte[] data) {
        RequestSendMessagePacket packet = new RequestSendMessagePacket(data);
        String message = packet.getMessage();

        MessagePacket messagePacket = new MessagePacket(client.getUsername(), message);
        client.getServerService().broadcast(messagePacket);
    }

    private void onRequestCharacterMove(byte[] data) {
        RequestCharacterMovePacket packet = new RequestCharacterMovePacket(data);
        Point3D newPos = packet.getPosition();

        PlayerInstance currentPlayer = client.getCurrentPlayer();
        currentPlayer.setPosition(newPos);

        ObjectPositionPacket objectPositionPacket = new ObjectPositionPacket(currentPlayer.getId(), newPos);
        client.getServerService().broadcast(objectPositionPacket, client);
    }

    private void onRequestLoadWorld() {
        client.sendPacket(new PlayerInfoPacket(client.getPlayer()));

        for (Map.Entry<Integer, PlayerInstance> pair : client.getWorldManagerService().getAllPlayers().entrySet()) {
            if(pair.getValue().getId() != client.getPlayer().getId()) {
                client.sendPacket(new UserInfoPacket(pair.getValue()));
            }
        }

        for (Map.Entry<Integer, NpcInstance> pair : client.getWorldManagerService().getAllNpcs().entrySet()) {
            client.sendPacket(new NpcInfoPacket(pair.getValue()));
        }
    }

    private void onRequestCharacterRotate(byte[] data) {
        RequestCharacterRotatePacket packet = new RequestCharacterRotatePacket(data);
        ObjectRotationPacket objectRotationPacket = new ObjectRotationPacket(
                client.getCurrentPlayer().getId(), packet.getAngle());
        client.getServerService().broadcast(objectRotationPacket, client);
    }

    private void onRequestCharacterAnimation(byte[] data) {
        RequestCharacterAnimationPacket packet = new RequestCharacterAnimationPacket(data);
        ObjectAnimationPacket objectAnimationPacket = new ObjectAnimationPacket(
                client.getCurrentPlayer().getId(), packet.getAnimId(), packet.getValue());
        client.getServerService().broadcast(objectAnimationPacket, client);
    }

    private void onRequestAttack(byte[] data) {
        RequestAttackPacket packet = new RequestAttackPacket(data);

        Entity entity = client.getWorldManagerService().getEntity(packet.getTargetId());
        entity.inflictDamage(1);

        ApplyDamagePacket applyDamagePacket = new ApplyDamagePacket(
                client.getCurrentPlayer().getId(), packet.getTargetId(), packet.getAttackType(), 1);
        client.getServerService().broadcastAll(applyDamagePacket);
    }

    private void onRequestCharacterMoveDirection(byte[] data) {
        RequestCharacterMoveDirection packet = new RequestCharacterMoveDirection(data);

        ObjectDirectionPacket objectDirectionPacket = new ObjectDirectionPacket(
                client.getCurrentPlayer().getId(), packet.getSpeed(), packet.getDirection());
        client.getServerService().broadcast(objectDirectionPacket, client);
    }
}
