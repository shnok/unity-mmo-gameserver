package com.shnok.javaserver.service;

import com.shnok.javaserver.model.object.entity.Entity;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class AttackStanceManagerService
{
    protected Map<Entity, Long> attackStanceTasks = new FastMap<Entity, Long>().shared();

    private static AttackStanceManagerService instance;

    public AttackStanceManagerService() {
        ThreadPoolManagerService.getInstance().scheduleAiAtFixedRate(new FightModeScheduler(), 0, 1000);
    }

    public static AttackStanceManagerService getInstance() {
        if (instance == null) {
            instance = new AttackStanceManagerService();
        }

        return instance;
    }

    public void addAttackStanceTask(Entity actor) {
        attackStanceTasks.put(actor, System.currentTimeMillis());
    }

    public void removeAttackStanceTask(Entity actor) {
        attackStanceTasks.remove(actor);
    }

    public boolean getAttackStanceTask(Entity actor) {
        return attackStanceTasks.containsKey(actor);
    }

    private class FightModeScheduler implements Runnable {
        protected FightModeScheduler() {
            // Do nothing
        }

        @Override
        public void run() {
            Long current = System.currentTimeMillis();
            try {
                if (attackStanceTasks != null) {
                    synchronized (this) {
                        for (Entity actor : attackStanceTasks.keySet()) {
                            if ((current - attackStanceTasks.get(actor)) > 15000) {
                                //actor.broadcastPacket(new AutoAttackStop(actor.getObjectId()));
                                //actor.getAI().setAutoAttacking(false);
                                attackStanceTasks.remove(actor);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                log.warn(e.toString());
            }
        }
    }
}
