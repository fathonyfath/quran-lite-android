package id.fathonyfath.quran.lite.utils.scheduler;

import android.os.Handler;
import android.os.Looper;

public class MainScheduler implements Scheduler {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable runnable) {
        this.handler.post(runnable);
    }
}
