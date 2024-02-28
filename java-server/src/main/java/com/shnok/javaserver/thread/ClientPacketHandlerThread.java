package com.shnok.javaserver.thread;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.db.entity.DBCharTemplate;
import com.shnok.javaserver.db.repository.CharTemplateRepository;
import com.shnok.javaserver.dto.clientpackets.*;
import com.shnok.javaserver.dto.serverpackets.*;
import com.shnok.javaserver.enums.ClientPacketType;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.enums.PlayerAction;
import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.template.PlayerTemplate;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.service.factory.PlayerFactoryService;
import com.shnok.javaserver.thread.ai.PlayerAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

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
        if(Config.PRINT_CLIENT_PACKETS_LOGS) {
            if(type != ClientPacketType.Ping) {
                log.debug("Received packet: {}", type);
            }
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
            case RequestAutoAttack:
                onRequestAutoAttack();
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

        // Notify known list
        ObjectPositionPacket objectPositionPacket = new ObjectPositionPacket(currentPlayer.getId(), newPos);
        client.getCurrentPlayer().broadcastPacket(objectPositionPacket);
    }

    private void onRequestLoadWorld() {
        client.setClientReady(true);
        System.out.println("On load world");

        PlayerInstance player = PlayerFactoryService.getInstance().getPlayerInstanceById(0);
        player.setGameClient(client);

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
        if(((Entity)object).getStatus().getHp() <= 0) {
            log.warn("Trying to attack a dead entity.");
            return;
        }

        // ! FOR DEBUG PURPOSE
        int damage = 25;
        ((Entity) object).inflictDamage(client.getCurrentPlayer(), damage);
        boolean critical = false;
        Random r = new Random();
        if(r.nextInt(2) == 0) {
            critical = true;
        }

        // ! FOR DEBUG PURPOSE
        // Notify known list
        ApplyDamagePacket applyDamagePacket = new ApplyDamagePacket(
                client.getCurrentPlayer().getId(), packet.getTargetId(), damage, ((Entity) object).getStatus().getHp(), critical);
        // Send packet to player's known list
        client.getCurrentPlayer().broadcastPacket(applyDamagePacket);
        // Send packet to player
        client.sendPacket(applyDamagePacket);
    }

    private void onRequestCharacterMoveDirection(byte[] data) {
        RequestCharacterMoveDirection packet = new RequestCharacterMoveDirection(data);
        if((client.getCurrentPlayer().isAttacking() ||
                client.getCurrentPlayer().getAi().getIntention() == Intention.INTENTION_ATTACK) && // if player attack animation is playing
                //client.getCurrentPlayer().getAi().getAttackTarget() != null && // if player has an attack target
                packet.getDirection().getX() != 0 && packet.getDirection().getZ() != 0) { // if direction is not zero
            log.warn("[{}] Player moved ({}), stop attacking. ", client.getCurrentPlayer().getId(), packet.getDirection());
            client.getCurrentPlayer().getAi().notifyEvent(Event.CANCEL);
        }

        // Notify known list
        ObjectDirectionPacket objectDirectionPacket = new ObjectDirectionPacket(
                client.getCurrentPlayer().getId(), client.getCurrentPlayer().getStatus().getMoveSpeed(), packet.getDirection());
        client.getCurrentPlayer().broadcastPacket(objectDirectionPacket);
    }

    private void onRequestSetTarget(byte[] data) {
        RequestSetTargetPacket packet = new RequestSetTargetPacket(data);

        if(packet.getTargetId() == -1) {
            // Clear target
            client.getCurrentPlayer().getAi().setTarget(null);
        } else {
            //TODO: find entity in all entities if missing in knownlist and add it

            // Check if user is allowed to target entity
            if(!client.getCurrentPlayer().getKnownList().knowsObject(packet.getTargetId())) {
                log.warn("[{}] Player tried to target an entity outside of this known list with ID [{}]",
                        client.getCurrentPlayer().getId(), packet.getTargetId());
                client.sendPacket(new ActionFailedPacket(PlayerAction.SetTarget.getValue()));
                return;
            }

            Entity target = (Entity) client.getCurrentPlayer().getKnownList().getKnownObject(packet.getTargetId());

            // Set entity target
            client.getCurrentPlayer().getAi().setTarget(target);
        }
    }

    private void onRequestAutoAttack() {
        Entity target = (Entity) client.getCurrentPlayer().getAi().getTarget();
        if(client.getCurrentPlayer().getAi().getTarget() == null) {
            log.warn("[{}] Player doesn't have a target", client.getCurrentPlayer().getId());
            client.sendPacket(new ActionFailedPacket(PlayerAction.AutoAttack.getValue()));
            return;
        }

        if(target.isDead() || client.getCurrentPlayer().isDead()) {
            log.warn("[{}] Either user or target is already dead", client.getCurrentPlayer().getId());
            client.sendPacket(new ActionFailedPacket(PlayerAction.AutoAttack.getValue()));
            return;
        }

        float distance = VectorUtils.calcDistance2D(client.getCurrentPlayer().getPos(), target.getPos());
        if(distance > client.getCurrentPlayer().getTemplate().getBaseAtkRange()) {
            log.warn("[{}] Player is too far from target {}/{}", client.getCurrentPlayer().getId(),
                    client.getCurrentPlayer().getTemplate().getBaseAtkRange(), distance);
            client.sendPacket(new ActionFailedPacket(PlayerAction.AutoAttack.getValue()));
            return;
        }

        PlayerAI ai = (PlayerAI) client.getCurrentPlayer().getAi();
        if(ai == null) {
            log.error("[{}] No AI is attached to player", client.getCurrentPlayer().getId());
            return;
        }

        //ai.setAttackTarget(target);
        ai.setIntention(Intention.INTENTION_ATTACK, target);
        ai.notifyEvent(Event.READY_TO_ACT);
    }
}
