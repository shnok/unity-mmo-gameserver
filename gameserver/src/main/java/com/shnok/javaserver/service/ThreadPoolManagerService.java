package com.shnok.javaserver.service;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.*;

@Log4j2
public class ThreadPoolManagerService {
    private ScheduledThreadPoolExecutor spawnThreadPool;
    private ScheduledThreadPoolExecutor aiThreadPool;
    private ScheduledThreadPoolExecutor effectsThreadPool;
    private ScheduledThreadPoolExecutor generalScheduledThreadPool;
    private ThreadPoolExecutor packetsThreadPool;
    private ThreadPoolExecutor generalThreadPool;
    private boolean shutdown = false;

    private static ThreadPoolManagerService instance;
    public static ThreadPoolManagerService getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManagerService();
        }
        return instance;
    }

    public void initialize() {
        log.info("Initializing thread pool manager service.");
        spawnThreadPool = new ScheduledThreadPoolExecutor(5);
        aiThreadPool = new ScheduledThreadPoolExecutor(100);
        effectsThreadPool = new ScheduledThreadPoolExecutor(100);
        generalScheduledThreadPool = new ScheduledThreadPoolExecutor(100);
        packetsThreadPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        generalThreadPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public void execute(Runnable r) {
        generalThreadPool.execute(r);
    }

    public ScheduledFuture<?> scheduleSpawn(Runnable r, long delay) {
        try {
            if (delay < 0) {
                delay = 0;
            }
            return spawnThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            return null; /* shutdown, ignore */
        }
    }

    public ScheduledFuture<?> scheduleDestroyObject(Runnable r, long delay) {
        try {
            if (delay < 0) {
                delay = 0;
            }
            return spawnThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            return null; /* shutdown, ignore */
        }
    }

    public ScheduledFuture<?> scheduleAiAtFixedRate(Runnable r, long initial, long delay) {
        try {
            if (delay < 0)
                delay = 0;
            if (initial < 0)
                initial = 0;

            return aiThreadPool.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            return null;
        }
    }

    public ScheduledFuture<?> scheduleAi(Runnable r, long delay) {
        try {
            return aiThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            return null;
        }
    }

    /**
     * Schedules an effect task to be executed at fixed rate.
     * @param r the task to execute
     * @param initialDelay the initial delay in the given time unit
     * @param period the period between executions in the given time unit
     * @param unit the time unit of the initialDelay and period parameters
     * @return a ScheduledFuture representing pending completion of the task, and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleEffectAtFixedRate(Runnable r, long initialDelay, long period, TimeUnit unit) {
        try {
            return effectsThreadPool.scheduleAtFixedRate(r, initialDelay, period, unit);
        } catch (RejectedExecutionException e) {
            return null; /* shutdown, ignore */
        }
    }

    /**
     * Schedules an effect task to be executed at fixed rate.
     * @param r the task to execute
     * @param initialDelay the initial delay in milliseconds
     * @param period the period between executions in milliseconds
     * @return a ScheduledFuture representing pending completion of the task, and whose get() method will throw an exception upon cancellation
     */
    public ScheduledFuture<?> scheduleEffectAtFixedRate(Runnable r, long initialDelay, long period) {
        return scheduleEffectAtFixedRate(r, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public void scheduleGeneralAtFixedRate(Runnable r, long initial, long delay) {
        try {
            if (delay < 0) {
                delay = 0;
            }
            if (initial < 0) {
                initial = 0;
            }
            generalScheduledThreadPool.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ignored) {
        }
    }

    public void handlePacket(Thread cph) {
        packetsThreadPool.execute(cph);
    }

    public void shutdown() {
        shutdown = true;
        try {
            spawnThreadPool.awaitTermination(1, TimeUnit.SECONDS);
            aiThreadPool.awaitTermination(1, TimeUnit.SECONDS);
            packetsThreadPool.shutdown();

            purge();
            
            log.info("All ThreadPools are now purged.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void purge() {
        spawnThreadPool.purge();
        aiThreadPool.purge();
        packetsThreadPool.purge();
        generalThreadPool.purge();
        generalScheduledThreadPool.purge();
    }
}
