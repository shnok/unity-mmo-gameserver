package com.shnok.javaserver.model.status;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@ToString
@NoArgsConstructor
public abstract class Status {
    protected int hp;
    protected int maxHp;
    protected int moveSpeed = 5;
    protected Entity owner;

    /** Array containing all clients that need to be notified about hp/mp updates of the L2Character */
    private Set<PlayerInstance> statusListener;

    protected Status(Entity owner, int maxHp) {
        this.owner = owner;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    /**
     * Add the object to the list of L2Character that must be informed of HP/MP updates of this L2Character.<br>
     * <B><U>Concept</U>:</B><br>
     * Each Entity owns a list called <B>statusListener</B> that contains all Entities to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this Entity.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.<br>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>Target a PC or NPC</li>
     * <ul>
     * @param object L2Character to add to the listener
     */
    public final void addStatusListener(PlayerInstance object) {
        if (object == owner) {
            return;
        }

        getStatusListener().add(object);
    }

    /**
     * Remove the object from the list of L2Character that must be informed of HP/MP updates of this L2Character.<br>
     * <B><U>Concept</U>:</B><br>
     * Each L2Character owns a list called <B>_statusListener</B> that contains all L2PcInstance to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this L2Character.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.<br>
     * <B><U>Example of use </U>:</B>
     * <ul>
     * <li>Untarget a PC or NPC</li>
     * </ul>
     * @param object L2Character to add to the listener
     */
    public final void removeStatusListener(PlayerInstance object) {
        getStatusListener().remove(object);
    }

    /**
     * Return the list of L2Character that must be informed of HP/MP updates of this L2Character.<br>
     * <B><U>Concept</U>:</B><br>
     * Each L2Character owns a list called <B>_statusListener</B> that contains all L2PcInstance to inform of HP/MP updates.<br>
     * Players who must be informed are players that target this L2Character.<br>
     * When a RegenTask is in progress sever just need to go through this list to send Server->Client packet StatusUpdate.
     * @return The list of L2Character to inform or null if empty
     */
    public final Set<PlayerInstance> getStatusListener() {
        if (statusListener == null) {
            statusListener = ConcurrentHashMap.newKeySet();
        }

        return statusListener;
    }

}
