package id.fathonyfath.quran.lite.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import id.fathonyfath.quran.lite.utils.scheduler.Schedulers;

public class SurahDownloaderService extends Service {

    private final static int NOTIFICATION_ID = 1;

    public static void startForegroundService(Context context) {
        final Intent intent = new Intent(context, SurahDownloaderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, DownloaderNotification.createNotification(this));

        final NotificationManager notificationManager = this.notificationManager;

        Schedulers.Computation().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    Schedulers.Main().execute(new Runnable() {
                        @Override
                        public void run() {
                            notificationManager.notify(NOTIFICATION_ID, DownloaderNotification.createCompleteNotification(SurahDownloaderService.this));
                            stopForeground(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        notificationManager = null;
    }

    @Override
    public Object getSystemService(String name) {
        Object service = super.getSystemService(name);
        if (service != null) {
            return service;
        }

        return getApplication().getSystemService(name);
    }
}
