package com.shnok.javaserver.thread;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.dto.clientpackets.*;
import com.shnok.javaserver.dto.serverpackets.*;
import com.shnok.javaserver.enums.ClientPacketType;
import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.PlayerInstance;
import com.shnok.javaserver.model.knownlist.ObjectKnownList;
import com.shnok.javaserver.model.status.PlayerStatus;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.ai.PlayerAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        if(Config.PRINT_CLIENT_PACKETS) {
            log.debug("Received packet: {}", type);
        }
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
            case RequestSetTarget:
                onRequestSetTarget(data);
                break;
        }
    }

    private void onReceiveEcho() {
        client.sendPacket(new PingPacket());

        Timer timer = new Timer(Config.CONNECTION_TIMEOUT_SEC, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (System.currentTimeMillis() - client.getLastEcho() >= Config.CONNECTION_TIMEOUT_SEC) {
                    log.info("User connection timeout.");
                    client.removeSelf();
                    client.disconnect();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();

        client.setLastEcho(System.currentTimeMillis(), timer);
    }

    private void onReceiveAuth(byte[] data) {
        AuthRequestPacket packet = new AuthRequestPacket(data);
        String username = packet.getUsername();

        AuthResponsePacket authResponsePacket;
        if (ServerService.getInstance().userExists(username)) {
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
        ServerService.getInstance().broadcast(messagePacket);
    }

    private void onRequestCharacterMove(byte[] data) {
        RequestCharacterMovePacket packet = new RequestCharacterMovePacket(data);
        Point3D newPos = packet.getPosition();

        PlayerInstance currentPlayer = client.getCurrentPlayer();
        currentPlayer.setPosition(newPos);

        //Todo : to remove
        //for debug purpose
//        try {
//            System.out.println("Node found at " + Geodata.getInstance().getNodeAt(newPos).getNodeIndex());
//        } catch (Exception e) {
//            System.out.println("Node not found at " + newPos);
//        }
        /*PathFinding.getInstance().findPath(client.getCurrentPlayer().getPos(), new Point3D(
                client.getCurrentPlayer().getPos().getX() + 5f,
                client.getCurrentPlayer().getPos().getY(),
                client.getCurrentPlayer().getPos().getZ()
        ));*/

        // Notify known list
        ObjectPositionPacket objectPositionPacket = new ObjectPositionPacket(currentPlayer.getId(), newPos);
        client.getCurrentPlayer().broadcastPacket(objectPositionPacket);
    }

    private void onRequestLoadWorld() {
        client.setClientReady(true);
        System.out.println("On load world");

        // Dummy player
        // TODO: FETCH FROM DB
        PlayerInstance player = new PlayerInstance(client.getUsername());
        player.setStatus(new PlayerStatus());
        player.setGameClient(client);
        player.setId(WorldManagerService.getInstance().nextID());
        player.setPosition(VectorUtils.randomPos(Config.PLAYER_SPAWN_POINT, 1.5f));

        // AI initialization
        PlayerAI ai = new PlayerAI();
        ai.setOwner(player);
        player.setAi(ai);

        client.setCurrentPlayer(player);

        WorldManagerService.getInstance().addPlayer(player);

        // Share character with client
        client.sendPacket(new PlayerInfoPacket(player));
        client.sendPacket(new GameTimePacket());

        // Loads surrounding area
        client.getCurrentPlayer().getKnownList().forceRecheckSurroundings();
    }

    private void onRequestCharacterRotate(byte[] data) {
        RequestCharacterRotatePacket packet = new RequestCharacterRotatePacket(data);

        // Notify known list
        ObjectRotationPacket objectRotationPacket = new ObjectRotationPacket(
                client.getCurrentPlayer().getId(), packet.getAngle());
        client.getCurrentPlayer().broadcastPacket(objectRotationPacket);
    }

    private void onRequestCharacterAnimation(byte[] data) {
        RequestCharacterAnimationPacket packet = new RequestCharacterAnimationPacket(data);

        // Notify known list
        ObjectAnimationPacket objectAnimationPacket = new ObjectAnimationPacket(
                client.getCurrentPlayer().getId(), packet.getAnimId(), packet.getValue());
        client.getCurrentPlayer().broadcastPacket(objectAnimationPacket);
    }

    private void onRequestAttack(byte[] data) {
        RequestAttackPacket packet = new RequestAttackPacket(data);

        GameObject object = client.getCurrentPlayer().getKnownList().getKnownObjects().get(packet.getTargetId());
        if(object == null) {
            log.warn("Trying to attack a null object.");
        }
        if(!(object instanceof Entity)) {
            log.warn("Trying to attack a non-entity object.");
            return;
        }

        ((Entity) object).inflictDamage(1);

        // Notify known list
        ApplyDamagePacket applyDamagePacket = new ApplyDamagePacket(
                client.getCurrentPlayer().getId(), packet.getTargetId(), packet.getAttackType(), 1);
        client.getCurrentPlayer().broadcastPacket(applyDamagePacket);
    }

    private void onRequestCharacterMoveDirection(byte[] data) {
        RequestCharacterMoveDirection packet = new RequestCharacterMoveDirection(data);

        // Notify known list
        ObjectDirectionPacket objectDirectionPacket = new ObjectDirectionPacket(
                client.getCurrentPlayer().getId(), packet.getSpeed(), packet.getDirection());
        client.getCurrentPlayer().broadcastPacket(objectDirectionPacket);
    }

    private void onRequestSetTarget(byte[] data) {
        RequestSetTargetPacket packet = new RequestSetTargetPacket(data);

        if(packet.getTargetId() == -1) {
            // Clear target
            client.getCurrentPlayer().getAi().setTarget(null);
        } else {
            // Find entity in entity list
            Entity target = WorldManagerService.getInstance().getEntity(packet.getTargetId());
            if(target == null) {
                log.warn("[{}] Player tried to target a wrong entity with ID [{}]",
                        client.getCurrentPlayer().getId(), packet.getTargetId());
                return;
            }

            // Check if user is allowed to target entity
            if(!client.getCurrentPlayer().getKnownList().knowsObject(target)) {
                log.warn("[{}] Player tried to target an entity outside of this known list with ID [{}]",
                        client.getCurrentPlayer().getId(), packet.getTargetId());
                return;
            }

            // Set entity target
            client.getCurrentPlayer().getAi().setTarget(target);
        }

        // Notify known list
        EntitySetTargetPacket entitySetTargetPacket = new EntitySetTargetPacket(
                client.getCurrentPlayer().getId(), packet.getTargetId());
        client.getCurrentPlayer().broadcastPacket(entitySetTargetPacket);
    }
}
