package com.shnok.javaserver.dto.external.clientpackets.authentication;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.ActionFailedPacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.dto.external.serverpackets.authentication.CharSelectionInfoPacket;
import com.shnok.javaserver.dto.external.serverpackets.authentication.RestartResponsePacket;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.service.AttackStanceManagerService;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RequestRestartPacket extends ClientPacket {

    public RequestRestartPacket(GameClientThread client, byte[] data) {
        super(client, data);

        handlePacket();
    }

    @Override
    public void handlePacket() {
        PlayerInstance player = client.getCurrentPlayer();
        if (player == null) {
            log.warn("[RequestRestart] activeChar null!?");
            return;
        }

        //TODO: Update DB
        //player.getInventory().updateDatabase();

        if (AttackStanceManagerService.getInstance().getAttackStanceTask(player)) {
            log.debug("Player {} tried to logout while fighting", player.getName());
            player.sendPacket(new SystemMessagePacket(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING));
            player.sendPacket(new ActionFailedPacket());
            return;
        }

        // detach the client from the char so that the connection isnt closed in the deleteMe
        player.setGameClient(null);

        // removing player from the world
        player.deleteMe();
        //L2GameClient.saveCharToDisk(client.getActiveChar());

        client.setCurrentPlayer(null);

        // return the client to the authed status
        client.setGameClientState(GameClientState.AUTHED);

        RestartResponsePacket response = new RestartResponsePacket();
        player.sendPacket(response);

        // send char list
        CharSelectionInfoPacket cl = new CharSelectionInfoPacket(client.getAccountName(), client.getSessionId().playOkID1);
        player.sendPacket(cl);

        client.setCharSelection(cl.getCharSelectData());
    }
}