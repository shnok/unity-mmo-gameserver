package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.external.clientpackets.*;
import com.shnok.javaserver.dto.external.clientpackets.authentication.*;
import com.shnok.javaserver.dto.external.clientpackets.item.*;
import com.shnok.javaserver.dto.external.serverpackets.authentication.PingPacket;
import com.shnok.javaserver.enums.network.packettypes.external.ClientPacketType;
import com.shnok.javaserver.security.NewCrypt;
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
        if(client.isCryptEnabled()) {
            if(!NewCrypt.verifyChecksum(data)) {
                log.warn("Packet's checksum is wrong.");
                return;
            }
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
            case RequestInventoryOpen:
                onRequestInventoryOpen();
                break;
            case RequestInventoryUpdateOrder:
                onRequestInventoryUpdateOrder();
                break;
            case UseItem:
                onUseItem();
                break;
            case RequestUnEquipItem:
                onRequestUnEquipItem();
                break;
            case RequestDestroyItem:
                onRequestDestroyItem();
                break;
            case RequestDropItem:
                onRequestDropItem();
                break;
            case RequestDisconnect:
                onRequestDisconnect();
                break;
            case RequestRestart:
                onRequestRestart();
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
        ProtocolVersionPacket packet = new ProtocolVersionPacket(client, data);
    }

    private void onReceiveAuth() {
        AuthLoginPacket packet = new AuthLoginPacket(client, data);
    }

    private void onReceiveMessage() {
        RequestSendMessagePacket packet = new RequestSendMessagePacket(client, data);
    }

    private void onRequestCharacterMove() {
        RequestCharacterMovePacket packet = new RequestCharacterMovePacket(client, data);
    }

    private void onRequestLoadWorld() {
        RequestLoadWorldPacket packet = new RequestLoadWorldPacket(client);
    }

    private void onRequestCharacterRotate() {
        RequestCharacterRotatePacket packet = new RequestCharacterRotatePacket(client, data);
    }

    private void onRequestCharacterAnimation() {
        RequestCharacterAnimationPacket packet = new RequestCharacterAnimationPacket(client, data);
    }

    private void onRequestAttack() {
        //RequestAttackPacket packet = new RequestAttackPacket(client, data);
    }

    private void onRequestCharacterMoveDirection() {
        RequestCharacterMoveDirection packet = new RequestCharacterMoveDirection(client, data);
    }

    private void onRequestSetTarget() {
        RequestSetTargetPacket packet = new RequestSetTargetPacket(client, data);
    }

    private void onRequestAutoAttack() {
        RequestAutoAttackPacket packet = new RequestAutoAttackPacket(client);
    }

    private void onRequestCharSelect() {
        RequestCharSelectPacket packet = new RequestCharSelectPacket(client, data);
    }

    private void onRequestInventoryOpen() {
        RequestInventoryOpenPacket packet = new RequestInventoryOpenPacket(client);
    }

    private void onRequestInventoryUpdateOrder() {
        RequestInventoryUpdateOrderPacket packet = new RequestInventoryUpdateOrderPacket(client, data);
    }

    private void onUseItem() {
        UseItemPacket packet = new UseItemPacket(client, data);
    }

    private void onRequestUnEquipItem() {
        RequestUnEquipItemPacket packet = new RequestUnEquipItemPacket(client, data);
    }

    private void onRequestDestroyItem() {
        RequestDestroyItemPacket packet = new RequestDestroyItemPacket(client, data);
    }

    private void onRequestDropItem() {

    }

    private void onRequestDisconnect() {
        DisconnectPacket packet = new DisconnectPacket(client);
    }

    private void onRequestRestart() {
        RequestRestartPacket packet = new RequestRestartPacket(client);
    }
}
