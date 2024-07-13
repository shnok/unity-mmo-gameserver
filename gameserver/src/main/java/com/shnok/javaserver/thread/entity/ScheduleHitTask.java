package com.shnok.javaserver.thread.entity;


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
    private final boolean shield;
    private final boolean soulshot;

    public ScheduleHitTask(Entity attacker, Entity hitTarget, int damage, boolean criticalHit,
                           boolean miss, boolean soulshot, boolean shield) {
        this.attacker = attacker;
        this.hitTarget = hitTarget;
        this.damage = damage;
        this.criticalHit = criticalHit;
        this.miss = miss;
        this.shield = shield;
        this.soulshot = soulshot;
    }

    @Override
    public void run() {
        try {
            attacker.onHitTimer(hitTarget, damage, criticalHit, miss, soulshot, shield);
        } catch (Throwable e) {
            log.error(e);
        }
    }
}