package id.fathonyfath.quran.lite.utils.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class IOScheduler implements Scheduler {

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void execute(Runnable runnable) {
        this.executor.execute(runnable);
    }
}
