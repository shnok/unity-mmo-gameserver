package com.shnok.javaserver.dto.external.clientpackets.authentication;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.PlayerInfoPacket;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.model.CharSelectInfoPackage;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class RequestCharSelectPacket extends ClientPacket {
    private final int charSlot;
    public RequestCharSelectPacket(GameClientThread client, byte[] data) {
        super(client, data);

        charSlot = readB();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        if(client.getCurrentPlayer() != null) {
            log.warn("Character already selected for account {}.", client.getAccountName());
            return;
        }

        final CharSelectInfoPackage info = client.getCharSelection(getCharSlot());
        if (info == null) {
            log.warn("Wrong character slot[{}] selected for account {}.", getCharSlot(), client.getAccountName());
            return;
        }

        //TODO check if banned
        // maybe check for dualbox limitations too...

        log.debug("Character slot {} selected for account {}.", getCharSlot(), client.getAccountName());

        PlayerInstance player = client.loadCharFromDisk(getCharSlot());
        if (player == null) {
            return; // handled in L2GameClient
        }
        WorldManagerService.getInstance().addPlayer(player);

        player.setGameClient(client);
        client.setCurrentPlayer(player);
        player.setOnlineStatus(true, true);

        client.setCharSlot(getCharSlot());

        client.setGameClientState(GameClientState.JOINING);

        PlayerInfoPacket cs = new PlayerInfoPacket(player);
        client.sendPacket(cs);
    }
}
