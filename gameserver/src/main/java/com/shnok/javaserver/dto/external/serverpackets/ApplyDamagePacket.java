package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.Hit;
import com.shnok.javaserver.model.object.entity.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApplyDamagePacket extends SendablePacket {
    private final List<Hit> hits = new ArrayList<>();
    private boolean soulshot = false;
    private byte ssGrade = 0;
    private final int attackerId;

    public ApplyDamagePacket(int attackerId, boolean soulshot, byte ssGrade) {
        super(ServerPacketType.ApplyDamage.getValue());
        this.attackerId = attackerId;
        this.soulshot = soulshot;
        this.ssGrade = ssGrade;
    }

    public boolean hasSoulshot() {
        return soulshot;
    }

    /**
     * Adds hit to the attack (Attacks such as dual dagger/sword/fist has two hits)
     * @param target
     * @param damage
     * @param miss
     * @param crit
     * @param shld
     */
    public void addHit(Entity target, int damage, boolean miss, boolean crit, byte shld) {
        hits.add(new Hit(target, damage, miss, crit, shld, soulshot, ssGrade));
    }

    /**
     * @return {@code true} if current attack contains at least 1 hit.
     */
    public boolean hasHits() {
        return !hits.isEmpty();
    }

    /**
     * Writes current hit
     * @param hit
     */
    private void writeHit(Hit hit) {
        writeI(hit.getTargetId());
        writeI(hit.getDamage());
        writeI(hit.getFlags());
    }

    public void writeMe() {
        final Iterator<Hit> it = hits.iterator();

        writeI(attackerId);

        writeB((byte) hits.size());
        while (it.hasNext()) {
            writeHit(it.next());
        }

        buildPacket();
    }
}