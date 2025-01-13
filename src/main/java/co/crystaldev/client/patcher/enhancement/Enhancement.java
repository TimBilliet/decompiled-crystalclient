package co.crystaldev.client.patcher.enhancement;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public interface Enhancement {
    public static final AtomicInteger counter = new AtomicInteger(0);

    ThreadPoolExecutor POOL = ThreadPoolInitializer.getThreadPool();
//
//  static {
//    POOL = new ThreadPoolExecutor(50, 50, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, String.format("Thread %s", new Object[] { Integer.valueOf(counter.incrementAndGet()) })));
//  }

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

/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\enhancement\Enhancement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */