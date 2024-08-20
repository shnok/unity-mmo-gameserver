package com.shnok.javaserver.dto.external.clientpackets.authentication;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.ActionFailedPacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.service.AttackStanceManagerService;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DisconnectPacket extends ClientPacket {

    public DisconnectPacket(GameClientThread client) {
        super(client, new byte[1]);

        handlePacket();
    }

    @Override
    public void handlePacket() {
        // Dont allow leaving if player is fighting
        PlayerInstance player = client.getCurrentPlayer();
        if (player == null) {
            log.warn("[DisconnectPacket] activeChar null!?");
            return;
        }

        // TODO update DB
        //player.getInventory().updateDatabase();

        if (AttackStanceManagerService.getInstance().getAttackStanceTask(player)) {
            log.debug("Player {} tried to logout while fighting", player.getName());
            player.sendPacket(new SystemMessagePacket(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING));
            player.sendPacket(new ActionFailedPacket());
            return;
        }

        // Close the connection with the client
        player.closeNetConnection();
        //notifyFriends(player);
    }
}