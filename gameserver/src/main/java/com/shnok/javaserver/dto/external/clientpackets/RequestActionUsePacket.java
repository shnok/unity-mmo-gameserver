package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.ActionFailedPacket;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RequestActionUsePacket extends ClientPacket {

    private final int actionId;

    public RequestActionUsePacket(GameClientThread client, byte[] data) {
        super(client, data);
        actionId = readI();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        PlayerInstance activeChar = client.getCurrentPlayer();
        if (activeChar == null) {
            return;
        }

        // dont do anything if player is dead
        if (activeChar.isAlikeDead()) {
            client.sendPacket(new ActionFailedPacket());
            return;
        }

        // don't do anything if player is confused
        if (activeChar.isOutOfControl()) {
            client.sendPacket(new ActionFailedPacket());
            return;
        }

        switch (actionId) {
            case 0:
                if (activeChar.isSitting()) {
                    activeChar.standUp();
                } else {
                    activeChar.sitDown();
                }

                log.debug("new wait type: {}", activeChar.isSitting() ? "SITTING" : "STANDING");

                break;
            case 1:
                if (activeChar.isRunning()) {
                    activeChar.setRunning(false);
                } else {
                    activeChar.setRunning(true);
                }

                log.debug("new move type: {}", activeChar.isRunning() ? "RUNNING" : "WALKIN");
                break;
        }
    }
}