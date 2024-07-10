package com.shnok.javaserver.model.status;

import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.PlayerStat;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.security.Rnd;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerStatus extends Status {
    private float currentCp = 0; // Current CP of the L2PcInstance

    public PlayerStatus(PlayerInstance activeChar) {
        super(activeChar);
    }

    public final void reduceCp(int value) {
        if (getCurrentCp() > value) {
            setCurrentCp(getCurrentCp() - value);
        } else {
            setCurrentCp(0);
        }
    }

    @Override
    public final void reduceHp(float value, Entity attacker) {
        reduceHp(value, attacker, true, false, false, false);
    }

    @Override
    public final void reduceHp(float value, Entity attacker, boolean awake, boolean isDOT, boolean isHPConsumption) {
        reduceHp(value, attacker, awake, isDOT, isHPConsumption, false);
    }

    public final void reduceHp(float value, Entity attacker, boolean awake, boolean isDOT, boolean isHPConsumption, boolean ignoreCP) {
        if (getOwner().isDead()) {
            return;
        }

        if ((getOwner().isInvul() || getOwner().isHpBlocked()) && !(isDOT || isHPConsumption)) {
            return;
        }

        if (!isHPConsumption) {
            getOwner().stopEffectsOnDamage(awake);
            // Attacked players in craft/shops stand up.
//            if (getOwner().isInCraftMode() || getOwner().isInStoreMode()) {
//                getOwner().setPrivateStoreType(PrivateStoreType.NONE);
//                getOwner().standUp();
//                getOwner().broadcastUserInfo();
//            } else
            if (((PlayerInstance) getOwner()).isSitting()) {
                ((PlayerInstance) getOwner()).standUp();
            }

            if (!isDOT) {
                if (getOwner().isStunned() && (Rnd.get(10) == 0)) {
                    getOwner().stopStunning(true);
                }
            }
        }

        int fullValue = (int) value;
        int tDmg = 0;

        if ((attacker != null) && (attacker != getOwner())) {
            final PlayerInstance attackerPlayer = (PlayerInstance) attacker;

//            if (attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage()) {
//                return;
//            }

            int mpDam = ((int) value * (int) getOwner().getStat().calcStat(Stats.MANA_SHIELD_PERCENT, 0, null, null)) / 100;
            if (mpDam > 0) {
                mpDam = (int) (value - mpDam);
                if (mpDam > getOwner().getCurrentMp()) {
                    ((PlayerInstance) getOwner()).sendPacket(SystemMessageId.MP_BECAME_0_ARCANE_SHIELD_DISAPPEARING);
                    getOwner().stopSkillEffects(true, 1556);
                    value = mpDam - getOwner().getCurrentMp();
                    getOwner().setCurrentMp(0, true);
                } else {
                    getOwner().reduceCurrentMp(mpDam);
                    SystemMessagePacket smsg = SystemMessagePacket.getSystemMessage(SystemMessageId.ARCANE_SHIELD_DECREASED_YOUR_MP_BY_S1_INSTEAD_OF_HP);
                    smsg.addInt(mpDam);
                    ((PlayerInstance) getOwner()).sendPacket(smsg);
                    return;
                }
            }

//            final PlayerInstance caster = getOwner().getTransferringDamageTo();
//            if ((caster != null) && ((PlayerInstance) getOwner()).getParty() != null &&
//                    VectorUtils.checkIfInRange(1000, getOwner(), caster, true) && !caster.isDead() && (getOwner() != caster) &&
//                    ((PlayerInstance) getOwner()).getParty().getMembers().contains(caster)) {
//                int transferDmg = ((int) value *
//                        (int) getOwner().getStat().calcStat(Stats.TRANSFER_DAMAGE_TO_PLAYER, 0, null, null)) / 100;
//                transferDmg = Math.min((int) caster.getCurrentHp() - 1, transferDmg);
//
//                if (transferDmg > 0) {
//                    int membersInRange = 0;
//                    for (PlayerInstance member : caster.getParty().getMembers()) {
//                        if (VectorUtils.checkIfInRange(1000, member, caster, false) && (member != caster)) {
//                            membersInRange++;
//                        }
//                    }
//
//                    if ((attacker instanceof PlayerInstance) && ((PlayerInstance) caster).getCurrentCp() > 0) {
//                        if (caster.getCurrentCp() > transferDmg) {
//                            caster.getStatus().reduceCp(transferDmg);
//                        } else {
//                            transferDmg = (int) (transferDmg - caster.getCurrentCp());
//                            caster.getStatus().reduceCp((int) caster.getCurrentCp());
//                        }
//                    }
//
//                    if (membersInRange > 0) {
//                        caster.reduceCurrentHp(transferDmg / membersInRange, attacker, null);
//                        value -= transferDmg;
//                        fullValue = (int) value;
//                    }
//                }
//            }

            if (!ignoreCP) {
                if (getCurrentCp() >= value) {
                    setCurrentCp(getCurrentCp() - value); // Set Cp to diff of Cp vs value
                    value = 0; // No need to subtract anything from Hp
                } else {
                    value -= getCurrentCp(); // Get diff from value vs Cp; will apply diff to Hp
                    setCurrentCp(0, false); // Set Cp to 0
                }
            }

            if ((fullValue > 0) && !isDOT) {
                // Send a System Message to the L2PcInstance
                SystemMessagePacket smsg = SystemMessagePacket.getSystemMessage(SystemMessageId.C1_RECEIVED_DAMAGE_OF_S3_FROM_C2);
                smsg.addString(((PlayerInstance)getOwner()).getName());
                smsg.addCharName(attacker);
                smsg.addInt(fullValue);
                ((PlayerInstance)getOwner()).sendPacket(smsg);

//                if ((tDmg > 0) && (attackerPlayer != null)) {
//                    smsg = SystemMessagePacket.getSystemMessage(SystemMessageId.GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_SERVITOR);
//                    smsg.addInt(fullValue);
//                    smsg.addInt(tDmg);
//                    attackerPlayer.sendPacket(smsg);
//                }
            }
        }

        if (value > 0) {
            value = getCurrentHp() - value;
            if (value <= 0) {
                value = 0;
            }
            setCurrentHp(value);
        }

        if ((getOwner().getCurrentHp() < 0.5) && !isHPConsumption) {
            getOwner().abortAttack();
            getOwner().abortCast();

            getOwner().doDie(attacker);
        }
    }

    @Override
    public final float getCurrentCp() {
        return currentCp;
    }

    @Override
    public final void setCurrentCp(float newCp) {
        setCurrentCp(newCp, true);
    }

    public final void setCurrentCp(float newCp, boolean broadcastPacket) {
        System.out.println("Set current cp: " + newCp);
        // Get the Max CP of the L2Character
        int oldCPValue = (int) getCurrentCp();
        int maxCp = getOwner().getStat().getMaxCp();

        synchronized (this) {
            if (getOwner().isDead()) {
                return;
            }

            if (newCp < 0) {
                newCp = 0;
            }

            if (newCp >= maxCp) {
                // Set the RegenActive flag to false
                currentCp = maxCp;
                flagsRegenActive &= ~REGEN_FLAG_CP;

                // Stop the HP/MP/CP Regeneration task
                if (flagsRegenActive == 0) {
                    stopHpMpRegeneration();
                }
            } else {
                // Set the RegenActive flag to true
                currentCp = newCp;
                flagsRegenActive |= REGEN_FLAG_CP;

                // Start the HP/MP/CP Regeneration task with Medium priority
                startHpMpRegeneration();
            }
        }

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
        if ((oldCPValue != currentCp) && broadcastPacket) {
            getOwner().broadcastStatusUpdate();
        }
    }

    @Override
    protected void doRegeneration() {
        final PlayerStat charstat = (PlayerStat) getOwner().getStat();

        // Modify the current CP of the L2Character and broadcast Server->Client packet StatusUpdate
        if (getCurrentCp() < charstat.getMaxRecoverableCp()) {
            setCurrentCp(getCurrentCp() + Formulas.calcCpRegen((PlayerInstance) getOwner()), false);
        }

        // Modify the current HP of the L2Character and broadcast Server->Client packet StatusUpdate
        if (getCurrentHp() < charstat.getMaxRecoverableHp()) {
            setCurrentHp(getCurrentHp() + Formulas.calcHpRegen(getOwner()), false);
        }

        // Modify the current MP of the L2Character and broadcast Server->Client packet StatusUpdate
        if (getCurrentMp() < charstat.getMaxRecoverableMp()) {
            setCurrentMp(getCurrentMp() + Formulas.calcMpRegen(getOwner()), false);
        }

        getOwner().broadcastStatusUpdate(); // send the StatusUpdate packet
    }
}
