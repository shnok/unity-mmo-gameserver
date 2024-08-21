package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.ActionFailedPacket;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.enums.PlayerAction;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.thread.GameClientThread;
import com.shnok.javaserver.thread.ai.PlayerAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RequestAutoAttackPacket extends ClientPacket {

    public RequestAutoAttackPacket(GameClientThread client) {
        super(client, new byte[1]);

        handlePacket();
    }

    @Override
    public void handlePacket() {
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