package com.shnok.javaserver.thread.entity;

import com.shnok.javaserver.model.object.entity.Entity;
import lombok.extern.log4j.Log4j2;

@Log4j2
// Task to destroy object based on delay
public class ScheduleDestroyTask implements Runnable {
    private final Entity entity;

    public ScheduleDestroyTask(Entity entity){
        this.entity = entity;
    }

    @Override
    public void run() {
        log.debug("Execute schedule destroy object");
        if (entity != null) {
            entity.destroy();
        }
    }
}
