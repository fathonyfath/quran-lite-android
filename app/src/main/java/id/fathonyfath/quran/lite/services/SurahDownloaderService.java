package id.fathonyfath.quran.lite.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import id.fathonyfath.quran.lite.models.Surah;
import id.fathonyfath.quran.lite.useCase.DownloadAllSurahUseCase;
import id.fathonyfath.quran.lite.useCase.UseCaseCallback;
import id.fathonyfath.quran.lite.useCase.UseCaseProvider;
import id.fathonyfath.quran.lite.utils.LocalBroadcastManager;

public class SurahDownloaderService extends Service implements UseCaseCallback<Integer>, DownloadAllSurahUseCase.SurahProgress {

    private final static int NOTIFICATION_ID = 1;

    private final static String ACTION_START = "ACTION_START";
    private final static String ACTION_STOP = "ACTION_STOP";

    public final static String ACTION_DOWNLOAD_FINISHED = "ACTION_DOWNLOAD_FINISHED";
    public final static String DOWNLOAD_FAILURE_COUNT = "DOWNLOAD_FAILURE_COUNT";

    public final static String ACTION_SERVICE_ALREADY_STARTED = "ACTION_SERVICE_ALREADY_STARTED";

    public static void startService(Context context) {
        final Intent intent = new Intent(context, SurahDownloaderService.class);
        intent.setAction(ACTION_START);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        final Intent intent = new Intent(context, SurahDownloaderService.class);
        intent.setAction(ACTION_STOP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private NotificationManager notificationManager;

    private DownloadAllSurahUseCase useCase;

    @Override
    public void onCreate() {
        super.onCreate();

        this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.useCase = UseCaseProvider.createUseCase(DownloadAllSurahUseCase.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) {
            action = "";
        }

        switch (action) {
            case ACTION_START:
                if (!useCase.isExecuted()) {
                    useCase.run();
                    startForeground(
                            NOTIFICATION_ID,
                            DownloaderNotification.createProgressNotification(
                                    this,
                                    "Memulai unduhan...",
                                    "",
                                    0,
                                    false
                            )
                    );
                } else {
                    final Intent alreadyStartedIntent = new Intent(ACTION_SERVICE_ALREADY_STARTED);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(alreadyStartedIntent);
                }
                break;
            case ACTION_STOP:
                stopForeground(false);
                stopSelf();
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.notificationManager = null;

        if (this.useCase != null) {
            this.useCase.cancel();
            this.useCase.setCallback(null);
            this.useCase.setSurahProgressCallback(null);
        }

        this.useCase = null;

        UseCaseProvider.clearUseCase(DownloadAllSurahUseCase.class);
    }

    @Override
    public Object getSystemService(String name) {
        Object service = super.getSystemService(name);
        if (service != null) {
            return service;
        }

        return getApplication().getSystemService(name);
    }

    @Override
    public void onProgress(Surah currentSurah, int currentSurahNumber, int maxSurahNumber, float progressPercentage) {
        notificationManager.notify(
                NOTIFICATION_ID,
                DownloaderNotification.createProgressNotification(
                        this,
                        "Unduhan sedang berjalan...",
                        currentSurahNumber + "/" + maxSurahNumber,
                        (int) progressPercentage,
                        false
                )
        );
    }

    @Override
    public void onProgress(float progress) {

    }

    @Override
    public void onResult(Integer data) {
        notificationManager.notify(
                NOTIFICATION_ID,
                DownloaderNotification.createProgressNotification(
                        this,
                        "Unduhan telah selesai...",
                        "",
                        100,
                        true
                )
        );

        final Intent intent = new Intent(ACTION_DOWNLOAD_FINISHED);
        intent.putExtra(DOWNLOAD_FAILURE_COUNT, data);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
