package com.shnok;

import java.util.concurrent.*;

public class ThreadPoolManager {
    private static ThreadPoolManager _instance;
    private final ScheduledThreadPoolExecutor _spawnThreadPool;
    private final ScheduledThreadPoolExecutor _aiThreadPool;
    private final ThreadPoolExecutor _packetsThreadPool;
    private boolean _shutdown = false;

    public ThreadPoolManager() {
        _spawnThreadPool = new ScheduledThreadPoolExecutor(5);
        _aiThreadPool = new ScheduledThreadPoolExecutor(100);
        _packetsThreadPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public static ThreadPoolManager getInstance() {
        if (_instance == null) {
            _instance = new ThreadPoolManager();
        }
        return _instance;
    }

    public ScheduledFuture<?> scheduleSpawn(Runnable r, long delay) {
        try {
            if (delay < 0) {
                delay = 0;
            }
            return _spawnThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
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

            return _aiThreadPool.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            return null;
        }
    }

    public void handlePacket(ClientPacketHandler cph) {
        _packetsThreadPool.execute(cph);
    }

    public void shutdown() {
        _shutdown = true;
        try {
            _spawnThreadPool.awaitTermination(1, TimeUnit.SECONDS);
            _aiThreadPool.awaitTermination(1, TimeUnit.SECONDS);
            _packetsThreadPool.shutdown();
            System.out.println("All ThreadPools are now stoped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isShutdown() {
        return _shutdown;
    }

    public void purge() {
        _spawnThreadPool.purge();
        _aiThreadPool.purge();
        _packetsThreadPool.purge();
    }
}
