package com.shnok;

import com.shnok.model.entities.Entity;
import javolution.util.FastList;

import java.util.List;

public class GameTimeController {
    public static final int TICKS_PER_SECOND = 10;
    public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
    private static GameTimeController _instance = new GameTimeController();
    protected static int _gameTicks;
    protected static long _gameStartTime;
    protected static TimerThread _timer;
    private static List<Entity> _movingObjects = new FastList<>();

    public static GameTimeController getInstance() {
        return _instance;
    }

    private GameTimeController()
    {
        _gameStartTime = System.currentTimeMillis() - 3600000; // offset so that the server starts a day begin
        _gameTicks = 3600000 / MILLIS_IN_TICK; // offset so that the server starts a day begin

        _timer = new TimerThread();
        _timer.start();
    }

    public int getGameTime() {
        return (_gameTicks / (TICKS_PER_SECOND * 10));
    }

    public static int getGameTicks() {
        return _gameTicks;
    }

    class TimerThread extends Thread
    {
        protected Exception _error;

        public TimerThread() {
            setDaemon(true);
            setPriority(MAX_PRIORITY);
        }

        @Override
        public void run() {
            try
            {
                for (;;) {
                    int _oldTicks = _gameTicks;
                    long runtime = System.currentTimeMillis() - _gameStartTime;

                    _gameTicks = (int) (runtime / MILLIS_IN_TICK); // new ticks value (ticks now)

                    if (_oldTicks != _gameTicks) {
                        moveObjects();
                        //System.out.println("Apply movement");
                    }

                    runtime = (System.currentTimeMillis() - _gameStartTime) - runtime;

                    int sleepTime = (1 + MILLIS_IN_TICK) - (((int) runtime) % MILLIS_IN_TICK);
                    sleep(sleepTime);
                }
            }
            catch (Exception e)
            {
                _error = e;
            }
        }
    }

    protected synchronized void moveObjects()
    {
        // Get all L2Character from the ArrayList movingObjects and put them into a table
        Entity[] entities = _movingObjects.toArray(new Entity[_movingObjects.size()]);

        for (Entity e : entities)  {
            boolean end = e.updatePosition(_gameTicks);
            if (end) {
                _movingObjects.remove(e);
            }
        }

    }

    public synchronized void addMovingObject(Entity e) {
        if (e == null) {
            return;
        }
        if (!_movingObjects.contains(e)) {
            _movingObjects.add(e);
        }
    }

    public void stopTimer() {
        _timer.interrupt();
    }
}
