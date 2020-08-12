package id.fathonyfath.quran.lite.utils.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class IOScheduler implements Scheduler {

    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 2;
    private static final int KEEP_ALIVE = 0;

    private final Executor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void execute(Runnable runnable) {
        this.executor.execute(runnable);
    }
}
