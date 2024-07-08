package com.shnok.javaserver.model.status;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.NpcInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
public class NpcStatus extends Status {
    public NpcStatus(NpcInstance activeChar) {
        super(activeChar);
    }

    @Override
    public void reduceHp(double value, Entity attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    @Override
    public void reduceHp(double value, Entity attacker, boolean awake, boolean isDOT, boolean isHpConsumption) {
        if (getOwner().isDead()) {
            return;
        }

        if (attacker != null) {
            final PlayerInstance attackerPlayer = (PlayerInstance) attacker;
            if (attackerPlayer.isInDuel()) {
                attackerPlayer.setDuelState(DuelState.INTERRUPTED);
            }

            // Add attackers to npc's attacker list
            getOwner().addAttackerToAttackByList(attacker);
        }

        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
    }
}
