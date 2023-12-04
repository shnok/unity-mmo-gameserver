package com.shnok.javaserver.service;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.model.entities.Entity;
import javolution.util.FastList;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class GameTimeControllerService {

    public static int TICKS_PER_SECOND;
    public static int MILLIS_IN_TICK;
    private static final List<Entity> movingObjects = new FastList<>();
    protected static int gameTicks;
    protected static long gameStartTime;
    protected static TimerThread timer;

    private static GameTimeControllerService instance;
    public static GameTimeControllerService getInstance() {
        if (instance == null) {
            instance = new GameTimeControllerService();
        }
        return instance;
    }

    public void initialize() {
        TICKS_PER_SECOND = Config.TIME_TICKS_PER_SECOND;
        MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;

        log.info("Starting server clock with a tick rate of {} ticks/second.", TICKS_PER_SECOND);

        gameStartTime = System.currentTimeMillis() - 3600000; // offset so that the server starts a day begin
        gameTicks = 3600000 / MILLIS_IN_TICK; // offset so that the server starts a day begin

        timer = new TimerThread();
        timer.start();
    }

    public static int getGameTicks() {
        return gameTicks;
    }

    public int getGameTime() {
        return (gameTicks / (TICKS_PER_SECOND * 10));
    }

    protected synchronized void moveObjects() {
        Entity[] entities = movingObjects.toArray(new Entity[0]);
        for (Entity e : entities) {
            if (e.updatePosition(gameTicks)) {
                movingObjects.remove(e);
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
        protected Exception error;

        public TimerThread() {
            setDaemon(true);
            setPriority(MAX_PRIORITY);
        }

        @Override
        public void run() {
            try {
                for (; ; ) {
                    int oldTicks = gameTicks;
                    long runtime = System.currentTimeMillis() - gameStartTime;

                    gameTicks = (int) (runtime / MILLIS_IN_TICK); // new ticks value (ticks now)

                    if (oldTicks != gameTicks) {
                        moveObjects();
                        //System.out.println("Apply movement");
                    }

                    runtime = (System.currentTimeMillis() - gameStartTime) - runtime;

                    int sleepTime = (1 + MILLIS_IN_TICK) - (((int) runtime) % MILLIS_IN_TICK);
                    sleep(sleepTime);
                }
            } catch (Exception e) {
                error = e;
            }
        }
    }
}
