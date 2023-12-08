package com.shnok.javaserver.service;

import com.shnok.javaserver.thread.ClientPacketHandlerThread;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.*;

@Log4j2
public class ThreadPoolManagerService {
    private ScheduledThreadPoolExecutor spawnThreadPool;
    private ScheduledThreadPoolExecutor aiThreadPool;
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

    public void handlePacket(ClientPacketHandlerThread cph) {
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
    }
}
