package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.external.clientpackets.*;
import com.shnok.javaserver.dto.external.serverpackets.*;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.enums.PlayerAction;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.enums.network.packettypes.external.ClientPacketType;
import com.shnok.javaserver.model.CharSelectInfoPackage;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.network.SessionKey;
import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.service.GameServerController;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.service.factory.PlayerFactoryService;
import com.shnok.javaserver.thread.ai.PlayerAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

import static com.shnok.javaserver.config.Configuration.server;

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
        if(client.isCryptEnabled()) {
            if(client.isPrintCryptography()) {
                log.debug("<--- [CLIENT] Encrypted packet {} : {}", data.length, Arrays.toString(data));
            }
            client.getGameCrypt().decrypt(data, 0, data.length);
            if(client.isPrintCryptography()) {
                log.debug("<--- [CLIENT] Decrypted packet {} : {}", data.length, Arrays.toString(data));
            }

            if(!NewCrypt.verifyChecksum(data)) {
                log.warn("Packet's checksum is wrong.");
                return;
            }
        } else if(client.isPrintCryptography()) {
            log.debug("<--- [CLIENT] Decrypted packet {} : {}", data.length, Arrays.toString(data));
        }


        ClientPacketType type = ClientPacketType.fromByte(data[0]);
        if(client.isPrintPacketsIn()) {
            if(type != ClientPacketType.Ping) {
                log.debug("[CLIENT] Received packet: {}", type);
            }
        }
        switch (type) {
            case Ping:
                onReceiveEcho();
                break;
            case ProtocolVersion:
                onReceiveProtocolVersion();
                break;
            case AuthLogin:
                onReceiveAuth();
                break;
            case SendMessage:
                onReceiveMessage();
                break;
            case RequestMove:
                onRequestCharacterMove();
                break;
            case LoadWorld:
                onRequestLoadWorld();
                break;
            case RequestRotate:
                onRequestCharacterRotate();
                break;
            case RequestAnim:
                onRequestCharacterAnimation();
                break;
            case RequestAttack:
                onRequestAttack();
                break;
            case RequestMoveDirection:
                onRequestCharacterMoveDirection();
                break;
            case RequestSetTarget:
                onRequestSetTarget();
                break;
            case RequestAutoAttack:
                onRequestAutoAttack();
                break;
            case CharSelect:
                onRequestCharSelect();
                break;
        }
    }

    private void onReceiveEcho() {
        client.sendPacket(new PingPacket());

        Timer timer = new Timer(client.getConnectionTimeoutMs() + 100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (System.currentTimeMillis() - client.getLastEcho() >= client.getConnectionTimeoutMs()) {
                    log.info("User connection timeout.");
                    client.disconnect();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();

        client.setLastEcho(System.currentTimeMillis(), timer);
    }

    private void onReceiveProtocolVersion() {
        ProtocolVersionPacket packet = new ProtocolVersionPacket(data);

        if (!server.allowedProtocolVersions().contains(packet.getVersion())) {
            log.warn("Received wrong protocol version: {}.", packet.getVersion());

            KeyPacket pk = new KeyPacket(client.enableCrypt(), false);
            client.sendPacket(pk);
            client.setProtocolOk(false);
            client.setCryptEnabled(true);
            client.disconnect();
        } else {
            log.debug("Client protocol version is ok: {}.", packet.getVersion());

            KeyPacket pk = new KeyPacket(client.enableCrypt(), true);
            client.sendPacket(pk);
            client.setProtocolOk(true);
            client.setCryptEnabled(true);
        }
    }

    private void onReceiveAuth() {
        AuthLoginPacket packet = new AuthLoginPacket(data);
        // avoid potential exploits
        if (client.getAccountName() == null) {
            SessionKey key = new SessionKey(packet.getLoginKey1(), packet.getLoginKey2(),
                    packet.getPlayKey1(), packet.getPlayKey2());

            log.info("Received auth request for account: {}.", packet.getAccount());
            // Preventing duplicate login in case client login server socket was disconnected or this packet was not sent yet
            if (LoginServerThread.getInstance().addLoggedAccount(packet.getAccount(), client)) {
                client.setAccountName(packet.getAccount());
                LoginServerThread.getInstance().addWaitingClientAndSendRequest(packet.getAccount(), client, key);
            } else {
                client.disconnect();
            }
        }
    }

    private void onReceiveMessage() {
        RequestSendMessagePacket packet = new RequestSendMessagePacket(data);
        String message = packet.getMessage();

        MessagePacket messagePacket = new MessagePacket(client.getAccountName(), message);
        GameServerController.getInstance().broadcast(messagePacket);
    }

    private void onRequestCharacterMove() {
        RequestCharacterMovePacket packet = new RequestCharacterMovePacket(data);
        Point3D newPos = packet.getPosition();

        PlayerInstance currentPlayer = client.getCurrentPlayer();
        currentPlayer.setPosition(newPos);
    }

    private void onRequestLoadWorld() {
        client.setClientReady(true);

        client.setGameClientState(GameClientState.IN_GAME);

        // Share character with client
        client.sendPacket(new GameTimePacket());

        // Load and notify surrounding entities
        Point3D spawnPos = client.getCurrentPlayer().getPosition().getWorldPosition();
        client.getCurrentPlayer().setPosition(spawnPos);

        //TODO: is it needed?
        client.getCurrentPlayer().getKnownList().forceRecheckSurroundings();

        client.authenticate();
    }

    private void onRequestCharacterRotate() {
        RequestCharacterRotatePacket packet = new RequestCharacterRotatePacket(data);

        // Notify known list
        ObjectRotationPacket objectRotationPacket = new ObjectRotationPacket(
                client.getCurrentPlayer().getId(), packet.getAngle());
        client.getCurrentPlayer().broadcastPacket(objectRotationPacket);
    }

    private void onRequestCharacterAnimation() {
        RequestCharacterAnimationPacket packet = new RequestCharacterAnimationPacket(data);

        // Notify known list
        ObjectAnimationPacket objectAnimationPacket = new ObjectAnimationPacket(
                client.getCurrentPlayer().getId(), packet.getAnimId(), packet.getValue());
        client.getCurrentPlayer().broadcastPacket(objectAnimationPacket);
    }

    private void onRequestAttack() {
        RequestAttackPacket packet = new RequestAttackPacket(data);

        //GameObject object = client.getCurrentPlayer().getKnownList().getKnownObjects().get(packet.getTargetId());
        GameObject object = client.getCurrentPlayer();
        if(object == null) {
            log.warn("Trying to attack a null object.");
        }
        if(!(object.isEntity())) {
            log.warn("Trying to attack a non-entity object.");
            return;
        }
        if(((Entity)object).getStatus().getCurrentHp() <= 0) {
            log.warn("Trying to attack a dead entity.");
            return;
        }

        // ! FOR DEBUG PURPOSE
        int damage = 25;
        ((Entity) object).reduceCurrentHp(damage, client.getCurrentPlayer());
        boolean critical = false;
        Random r = new Random();
        if(r.nextInt(2) == 0) {
            critical = true;
        }

        // ! FOR DEBUG PURPOSE
        // Notify known list
        ApplyDamagePacket applyDamagePacket = new ApplyDamagePacket(
                client.getCurrentPlayer().getId(), packet.getTargetId(), damage, ((Entity) object).getStatus().getCurrentHp(), critical);
        // Send packet to player's known list
        client.getCurrentPlayer().broadcastPacket(applyDamagePacket);
        // Send packet to player
        client.sendPacket(applyDamagePacket);
    }

    private void onRequestCharacterMoveDirection() {
        RequestCharacterMoveDirection packet = new RequestCharacterMoveDirection(data);
        if((client.getCurrentPlayer().isAttacking() ||
                client.getCurrentPlayer().getAi().getIntention() == Intention.INTENTION_ATTACK)) { // if player attack animation is playing
                //client.getCurrentPlayer().getAi().getAttackTarget() != null && // if player has an attack target
               //&& packet.getDirection().getX() != 0 && packet.getDirection().getZ() != 0) { // if direction is not zero
            log.warn("[{}] Player moved ({}), stop attacking. ", client.getCurrentPlayer().getId(), packet.getDirection());
            client.getCurrentPlayer().getAi().notifyEvent(Event.CANCEL);
        }

        if(packet.getDirection().getX() == 0 && packet.getDirection().getZ() == 0) {
            client.getCurrentPlayer().getAi().setIntention(Intention.INTENTION_IDLE);
        } else {
            client.getCurrentPlayer().getAi().setIntention(Intention.INTENTION_MOVE_TO);
        }

        // Notify known list
        ObjectDirectionPacket objectDirectionPacket = new ObjectDirectionPacket(
                client.getCurrentPlayer().getId(), client.getCurrentPlayer().getStat().getMoveSpeed(), packet.getDirection());
        client.getCurrentPlayer().broadcastPacket(objectDirectionPacket);

        // calculate heading
        if(packet.getDirection().getX() != 0 || packet.getDirection().getZ() != 0) {
            client.getCurrentPlayer().getPosition().setHeading(
                    VectorUtils.calculateMoveDirectionAngle(packet.getDirection().getX(), packet.getDirection().getZ()));
        }
    }

    private void onRequestSetTarget() {
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

    private void onRequestCharSelect() {
        RequestCharSelectPacket packet = new RequestCharSelectPacket(data);

        if(client.getCurrentPlayer() != null) {
            log.warn("Character already selected for account {}.", client.getAccountName());
            return;
        }

        final CharSelectInfoPackage info = client.getCharSelection(packet.getCharSlot());
        if (info == null) {
            log.warn("Wrong character slot[{}] selected for account {}.", packet.getCharSlot(), client.getAccountName());
            return;
        }

        //TODO check if banned
        // maybe check for dualbox limitations too...

        log.debug("Character slot {} selected for account {}.", packet.getCharSlot(), client.getAccountName());

        PlayerInstance player = client.loadCharFromDisk(packet.getCharSlot());
        if (player == null) {
            return; // handled in L2GameClient
        }
        WorldManagerService.getInstance().addPlayer(player);

        player.setGameClient(client);
        client.setCurrentPlayer(player);
        player.setOnlineStatus(true, true);

        client.setCharSlot(packet.getCharSlot());

        client.setGameClientState(GameClientState.JOINING);

        PlayerInfoPacket cs = new PlayerInfoPacket(player);
        client.sendPacket(cs);
    }
}
