package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.authentication.ActionAllowedPacket;
import com.shnok.javaserver.dto.external.serverpackets.ActionFailedPacket;
import com.shnok.javaserver.dto.external.serverpackets.ObjectDirectionPacket;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.enums.PlayerAction;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.thread.GameClientThread;
import com.shnok.javaserver.util.VectorUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class RequestCharacterMoveDirection extends ClientPacket {
    private final Point3D direction = new Point3D();

    public RequestCharacterMoveDirection(GameClientThread client, byte[] data) {
        super(client, data);
        direction.setX(readF());
        direction.setY(readF());
        direction.setZ(readF());

        handlePacket();
    }

    @Override
    public void handlePacket() {
        if(client.getCurrentPlayer().isAnimationLocked()) { // if player attack animation is playing
            log.warn("[{}] Player tried to move but is animation locked.", client.getCurrentPlayer().getId());

            client.getCurrentPlayer().sendPacket(new ActionFailedPacket(PlayerAction.Move.getValue()));

            // Stop the player movement
            ObjectDirectionPacket objectDirectionPacket = new ObjectDirectionPacket(
                    client.getCurrentPlayer().getId(), client.getCurrentPlayer().getStat().getMoveSpeed(),
                    new Point3D(0, getDirection().getY(), 0));

            client.getCurrentPlayer().broadcastPacket(objectDirectionPacket);

            return;
        }

        if((client.getCurrentPlayer().isAttacking() ||
                client.getCurrentPlayer().getAi().getIntention() == Intention.INTENTION_ATTACK)) { // if player is attacking
            //client.getCurrentPlayer().getAi().getAttackTarget() != null && // if player has an attack target
            //&& getDirection().getX() != 0 && getDirection().getZ() != 0) { // if direction is not zero
            log.warn("[{}] Player moved ({}), stop attacking. ", client.getCurrentPlayer().getId(), getDirection());

            client.getCurrentPlayer().sendPacket(new ActionAllowedPacket(PlayerAction.Move.getValue()));

            client.getCurrentPlayer().getAi().notifyEvent(Event.CANCEL);
        }

        if(getDirection().getX() == 0 && getDirection().getZ() == 0) {
            client.getCurrentPlayer().getAi().setIntention(Intention.INTENTION_IDLE);
        } else {
            client.getCurrentPlayer().getAi().setIntention(Intention.INTENTION_MOVE_TO);
        }

        // Notify known list
        ObjectDirectionPacket objectDirectionPacket = new ObjectDirectionPacket(
                client.getCurrentPlayer().getId(), client.getCurrentPlayer().getStat().getMoveSpeed(), getDirection());
        client.getCurrentPlayer().broadcastPacket(objectDirectionPacket);

        // calculate heading
        if(getDirection().getX() != 0 || getDirection().getZ() != 0) {
            client.getCurrentPlayer().getPosition().setHeading(
                    VectorUtils.calculateMoveDirectionAngle(getDirection().getX(), getDirection().getZ()));
        }
    }
}