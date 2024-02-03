package com.shnok.javaserver.service;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.util.TimeUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Getter
@Setter
public class GameTimeControllerService {
    public int ticksPerSecond;
    private int tickDurationMs;
    private List<Entity> movingObjects;
    public long gameTicks;
    private long gameStartTime;
    private float gameTime;
    private static TimerThread timer;

    private static GameTimeControllerService instance;
    public static GameTimeControllerService getInstance() {
        if (instance == null) {
            instance = new GameTimeControllerService();
        }
        return instance;
    }

    public void initialize() {
        ticksPerSecond = Config.TIME_TICKS_PER_SECOND;
        tickDurationMs = 1000 / ticksPerSecond;

        log.info("Starting server clock with a tick rate of {} ticks/second.", ticksPerSecond);

        gameStartTime = System.currentTimeMillis() - 3600000; // offset so that the server starts a day begin
        gameTicks = 3600000 / tickDurationMs; // offset so that the server starts a day begin

        movingObjects = new ArrayList<>();
        timer = new TimerThread();
        timer.start();
    }

    protected synchronized void moveObjects() {
        Entity[] entities = movingObjects.toArray(new Entity[0]);
        for (Entity e : entities) {
            try {
                if (e.updatePosition(gameTicks)) {
                    movingObjects.remove(e);
                }
            } catch (NullPointerException ex) {
                log.error("One entity is null in moving objects");
            }
        }
    }

    public synchronized void addMovingObject(Entity e) {
        if (e == null) {
            return;
        }
        if (!movingObjects.contains(e)) {
            movingObjects.add(e);
        }
    }

    public synchronized void removeMovingObject(Entity e) {
        if (e == null) {
            return;
        }
        movingObjects.remove(e);
    }

    public void stopTimer() {
        timer.interrupt();
    }

    class TimerThread extends Thread {
        public TimerThread() {
            setDaemon(true);
            setPriority(MAX_PRIORITY);
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    long oldTicks = gameTicks;
                    long runtime = System.currentTimeMillis() - gameStartTime;

                    gameTicks = (int) (runtime / tickDurationMs); // new ticks value (ticks now)
                    gameTime = TimeUtils.ticksToHour(gameTicks, tickDurationMs, Config.TIME_DAY_DURATION_MINUTES);
                    if (oldTicks != gameTicks) {
                        moveObjects();
                    }

                    runtime = (System.currentTimeMillis() - gameStartTime) - runtime;

                    int sleepTime = (1 + tickDurationMs) - (((int) runtime) % tickDurationMs);
                    sleep(sleepTime);
                }
            } catch (Exception e) {
                log.error("Game tick loop crashed. Reason: ", e);
            }
        }
    }
}
