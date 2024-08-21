package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.ActionFailedPacket;
import com.shnok.javaserver.enums.PlayerAction;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class RequestSetTargetPacket extends ClientPacket {
    private final int targetId;

    public RequestSetTargetPacket(GameClientThread client, byte[] data) {
        super(client, data);

        targetId = readI();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        if(getTargetId() == -1) {
            // Clear target
            client.getCurrentPlayer().getAi().setTarget(null);
        } else {
            //TODO: find entity in all entities if missing in knownlist and add it

            // Check if user is allowed to target entity
            if(!client.getCurrentPlayer().getKnownList().knowsObject(getTargetId())) {
                log.warn("[{}] Player tried to target an entity outside of this known list with ID [{}]",
                        client.getCurrentPlayer().getId(), getTargetId());
                client.sendPacket(new ActionFailedPacket(PlayerAction.SetTarget.getValue()));
                return;
            }

            Entity target = (Entity) client.getCurrentPlayer().getKnownList().getKnownObject(getTargetId());

            // Set entity target
            client.getCurrentPlayer().getAi().setTarget(target);
        }
    }
}
