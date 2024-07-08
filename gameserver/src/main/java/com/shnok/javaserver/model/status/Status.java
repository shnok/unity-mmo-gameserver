package com.shnok.javaserver.model.status;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Data
@ToString
@NoArgsConstructor
@Log4j2
public class Status {
    protected Entity owner;
    protected int currentHp;
    protected int currentMp;

    /** Array containing all clients that need to be notified about hp/mp updates of the Entity */
    private Set<PlayerInstance> statusListener;
    private Future<?> regTask;
    protected byte flagsRegenActive = 0;
    protected static final byte REGEN_FLAG_CP = 4;
    private static final byte REGEN_FLAG_HP = 1;
    private static final byte REGEN_FLAG_MP = 2;

    public Status(Entity owner) {
        this.owner = owner;
    }

    /**
     * Add the object to the list of Entity that must be informed of HP/MP updates of this Entity.<br>
     * <B><U>Concept</U>:</B><br>
     * Each Entity owns a list called <B>statusListener</B> that contains all Entities to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this Entity.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.<br>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>Target a PC or NPC</li>
     * <ul>
     * @param object Entity to add to the listener
     */
    public final void addStatusListener(PlayerInstance object) {
        if (object == owner) {
            return;
        }

        getStatusListener().add(object);
    }

    /**
     * Remove the object from the list of Entity that must be informed of HP/MP updates of this Entity.<br>
     * <B><U>Concept</U>:</B><br>
     * Each Entity owns a list called <B>_statusListener</B> that contains all PlayerInstance to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this Entity.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.<br>
     * <B><U>Example of use </U>:</B>
     * <ul>
     * <li>Untarget a PC or NPC</li>
     * </ul>
     * @param object Entity to add to the listener
     */
    public final void removeStatusListener(PlayerInstance object) {
        getStatusListener().remove(object);
    }

    /**
     * Return the list of Entity that must be informed of HP/MP updates of this Entity.<br>
     * <B><U>Concept</U>:</B><br>
     * Each Entity owns a list called <B>_statusListener</B> that contains all PlayerInstance to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this Entity.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.
     * @return The list of Entity to inform or null if empty
     */
    public final Set<PlayerInstance> getStatusListener() {
        if (statusListener == null) {
            statusListener = ConcurrentHashMap.newKeySet();
        }

        return statusListener;
    }

    // place holder, only PcStatus has CP
    public void reduceCp(int value) {
    }

    /**
     * Reduce the current HP of the Entity and launch the doDie Task if necessary.
     * @param value
     * @param attacker
     */
    public void reduceHp(double value, Entity attacker) {
        reduceHp(value, attacker, true, false, false);
    }

    public void reduceHp(double value, Entity attacker, boolean isHpConsumption) {
        reduceHp(value, attacker, true, false, isHpConsumption);
    }

    public void reduceHp(double value, Entity attacker, boolean awake, boolean isDOT, boolean isHPConsumption) {
        if (getOwner().isDead()) {
            return;
        }

        // invul handling
        if ((getOwner().isInvul() || getOwner().isHpBlocked()) && !(isDOT || isHPConsumption)) {
            return;
        }

        if (attacker != null) {
            final PlayerInstance attackerPlayer = (PlayerInstance) attacker;
            if (attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage()) {
                return;
            }
        }

        if (!isDOT && !isHPConsumption) {
            getOwner().stopEffectsOnDamage(awake);
            if (getOwner().isStunned() && (Rnd.get(10) == 0)) {
                getOwner().stopStunning(true);
            }
        }

        if (value > 0) {
            setCurrentHp(Math.max(getCurrentHp() - value, 0));
        }

        if ((getOwner().getCurrentHp() < 0.5) && getOwner().isMortal()) // Die
        {
            getOwner().abortAttack();
            getOwner().abortCast();

            log.debug("char is dead.");

            getOwner().doDie(attacker);
        }
    }

    public void reduceMp(double value) {
        setCurrentMp(Math.max(getCurrentMp() - value, 0));
    }

    /**
     * Start the HP/MP/CP Regeneration task.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Calculate the regen task period</li>
     * <li>Launch the HP/MP/CP Regeneration task with Medium priority</li>
     * </ul>
     */
    public final synchronized void startHpMpRegeneration() {
        if ((regTask == null) && !getOwner().isDead()) {
            log.debug("HP/MP regen started");

            // Get the Regeneration period
            int period = Formulas.getRegeneratePeriod(getOwner());

            // Create the HP/MP/CP Regeneration task
            regTask = ThreadPoolManagerService.getInstance().scheduleEffectAtFixedRate(new RegenTask(), period, period);
        }
    }

    /**
     * Stop the HP/MP/CP Regeneration task.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Set the RegenActive flag to False</li>
     * <li>Stop the HP/MP/CP Regeneration task</li>
     * </ul>
     */
    public final synchronized void stopHpMpRegeneration() {
        if (regTask != null) {
            log.debug("HP/MP regen stop");

            // Stop the HP/MP/CP Regeneration task
            regTask.cancel(false);
            regTask = null;

            // Set the RegenActive flag to false
            flagsRegenActive = 0;
        }
    }

    // place holder, only PcStatus has CP
    public double getCurrentCp() {
        return 0;
    }

    // place holder, only PcStatus has CP
    public void setCurrentCp(double newCp) {
    }
    
    public final void setCurrentHp(double newHp) {
        setCurrentHp(newHp, true);
    }

    /**
     * Sets the current hp of this character.
     * @param newHp the new hp
     * @param broadcastPacket if true StatusUpdate packet will be broadcasted.
     * @return @{code true} if hp was changed, @{code false} otherwise.
     */
    public boolean setCurrentHp(double newHp, boolean broadcastPacket) {
        // Get the Max HP of the Entity
        int lastHpValue = (int) getCurrentHp();
        final double maxHp = getOwner().getMaxHp();

        synchronized (this) {
            if (getOwner().isDead()) {
                return false;
            }

            if (newHp >= maxHp) {
                // Set the RegenActive flag to false
                currentHp = maxHp;
                flagsRegenActive &= ~REGEN_FLAG_HP;

                // Stop the HP/MP/CP Regeneration task
                if (flagsRegenActive == 0) {
                    stopHpMpRegeneration();
                }
            } else {
                // Set the RegenActive flag to true
                currentHp = newHp;
                flagsRegenActive |= REGEN_FLAG_HP;

                // Start the HP/MP/CP Regeneration task with Medium priority
                startHpMpRegeneration();
            }
        }

        boolean hpWasChanged = lastHpValue != currentHp;

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other PlayerInstance to inform
        if (hpWasChanged && broadcastPacket) {
            getOwner().broadcastStatusUpdate();
        }

        return hpWasChanged;
    }

    public final void setCurrentHpMp(double newHp, double newMp) {
        boolean hpOrMpWasChanged = setCurrentHp(newHp, false);
        hpOrMpWasChanged |= setCurrentMp(newMp, false);
        if (hpOrMpWasChanged) {
            getOwner().broadcastStatusUpdate();
        }
    }
    
    public final void setCurrentMp(double newMp) {
        setCurrentMp(newMp, true);
    }

    /**
     * Sets the current mp of this character.
     * @param newMp the new mp
     * @param broadcastPacket if true StatusUpdate packet will be broadcasted.
     * @return @{code true} if mp was changed, @{code false} otherwise.
     */
    public final boolean setCurrentMp(double newMp, boolean broadcastPacket) {
        // Get the Max MP of the Entity
        int lastMpValue = (int) getCurrentMp();
        final int maxMp = getOwner().getMaxMp();

        synchronized (this) {
            if (getOwner().isDead()) {
                return false;
            }

            if (newMp >= maxMp) {
                // Set the RegenActive flag to false
                currentMp = maxMp;
                flagsRegenActive &= ~REGEN_FLAG_MP;

                // Stop the HP/MP/CP Regeneration task
                if (_flagsRegenActive == 0) {
                    stopHpMpRegeneration();
                }
            } else {
                // Set the RegenActive flag to true
                currentMp = newMp;
                flagsRegenActive |= REGEN_FLAG_MP;

                // Start the HP/MP/CP Regeneration task with Medium priority
                startHpMpRegeneration();
            }
        }

        boolean mpWasChanged = lastMpValue != currentMp;

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other PlayerInstance to inform
        if (mpWasChanged && broadcastPacket) {
            getOwner().broadcastStatusUpdate();
        }

        return mpWasChanged;
    }

    protected void doRegeneration() {
        // Modify the current HP of the Entity and broadcast Server->Client packet StatusUpdate
        if (getCurrentHp() < getOwner().getMaxRecoverableHp()) {
            setCurrentHp(getCurrentHp() + Formulas.calcHpRegen(getOwner()), false);
        }

        // Modify the current MP of the Entity and broadcast Server->Client packet StatusUpdate
        if (getCurrentMp() < getOwner().getMaxRecoverableMp()) {
            setCurrentMp(getCurrentMp() + Formulas.calcMpRegen(getOwner()), false);
        }

        if ((getCurrentHp() >= getOwner().getMaxRecoverableHp()) && (getCurrentMp() >= getOwner().getMaxMp())) {
            stopHpMpRegeneration();
        }

        if (getOwner().isInActiveRegion()) {
            getOwner().broadcastStatusUpdate();
        }
    }

    /** Task of HP/MP regeneration */
    class RegenTask implements Runnable {
        @Override
        public void run() {
            try {
                doRegeneration();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}
