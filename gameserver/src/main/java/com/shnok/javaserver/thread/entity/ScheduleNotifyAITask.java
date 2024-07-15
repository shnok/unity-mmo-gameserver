package com.shnok.javaserver.thread.entity;

import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.thread.ai.BaseAI;
import lombok.extern.log4j.Log4j2;

@Log4j2
// Task to notify AI based on delay
public class ScheduleNotifyAITask implements Runnable {

    private final Event event;
    private final BaseAI ai;

    public ScheduleNotifyAITask(BaseAI ai, Event event) {
        this.event = event;
        this.ai = ai;
    }

    @Override
    public void run() {
        try {
            ai.notifyEvent(event, null);
        } catch (Throwable t) {
            log.warn(t);
        }
    }
}
