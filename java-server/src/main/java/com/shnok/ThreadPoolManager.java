package com.shnok;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {
    private static ThreadPoolManager _instance;
    private final ScheduledThreadPoolExecutor _spawnThreadPool;
    private final ThreadPoolExecutor _packetsThreadPool;
    private boolean _shutdown = false;

    public static ThreadPoolManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new ThreadPoolManager();
        }
        return _instance;
    }

    public ThreadPoolManager() {
        _spawnThreadPool = new ScheduledThreadPoolExecutor(5);
        _packetsThreadPool = new ScheduledThreadPoolExecutor(5);
    }

    public ScheduledFuture<?> scheduleSpawn(Runnable r, long delay)
    {
        try
        {
            if (delay < 0)
            {
                delay = 0;
            }
            return _spawnThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException e)
        {
            return null; /* shutdown, ignore */
        }
    }

    public void handlePacket(ClientPacketHandler cph)
    {
        _packetsThreadPool.execute(cph);
    }

    public void shutdown()
    {
        _shutdown = true;
        try
        {
            _spawnThreadPool.awaitTermination(1, TimeUnit.SECONDS);
            _packetsThreadPool.shutdown();
            System.out.println("All ThreadPools are now stoped");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isShutdown()
    {
        return _shutdown;
    }

    public void purge()
    {
        _spawnThreadPool.purge();
        _packetsThreadPool.purge();
    }
}
