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
}
