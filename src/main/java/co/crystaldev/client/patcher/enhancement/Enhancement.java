package co.crystaldev.client.patcher.enhancement;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public interface Enhancement {
    AtomicInteger counter = new AtomicInteger(0);

    ThreadPoolExecutor POOL = ThreadPoolInitializer.getThreadPool();


    default void tick() {
    }

    String getName();
}

class ThreadPoolInitializer {
    private static final AtomicInteger counter = new AtomicInteger();

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            50,
            50,
            0L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            r -> new Thread(r, String.format("Thread %s", counter.incrementAndGet()))
    );

    // Public method to access the thread pool
    public static ThreadPoolExecutor getThreadPool() {
        return POOL;
    }
}
