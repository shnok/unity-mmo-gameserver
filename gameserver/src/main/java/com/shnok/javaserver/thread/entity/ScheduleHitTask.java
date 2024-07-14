package com.shnok.javaserver.thread.entity;


import com.shnok.javaserver.dto.external.serverpackets.ApplyDamagePacket;
import com.shnok.javaserver.model.object.entity.Entity;
import lombok.extern.log4j.Log4j2;

@Log4j2
// Task to apply damage based on delay
public class ScheduleHitTask implements Runnable {
    private final Entity attacker;
    private final Entity hitTarget;
    private final int damage;
    private final boolean criticalHit;
    private final boolean miss;
    private final byte shield;
    private final boolean soulshot;
    private final ApplyDamagePacket attack;

    public ScheduleHitTask(ApplyDamagePacket attack, Entity attacker, Entity hitTarget, int damage, boolean criticalHit,
                           boolean miss, boolean soulshot, byte shield) {
        this.attacker = attacker;
        this.hitTarget = hitTarget;
        this.damage = damage;
        this.criticalHit = criticalHit;
        this.miss = miss;
        this.shield = shield;
        this.soulshot = soulshot;
        this.attack = attack;
    }

    @Override
    public void run() {
        try {
            attacker.onHitTimer(attack, hitTarget, damage, criticalHit, miss, soulshot, shield);
        } catch (Throwable e) {
            log.error(e);
            e.printStackTrace();
        }
    }
}