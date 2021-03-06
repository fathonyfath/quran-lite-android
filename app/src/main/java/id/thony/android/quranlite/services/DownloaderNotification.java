package id.thony.android.quranlite.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

public class DownloaderNotification {

    private static final String CHANNEL_ID = "SurahDownloadNotificationChannel";

    public static Notification createProgressNotification(Context context,
                                                          String title,
                                                          String description,
                                                          int currentProgress,
                                                          boolean isFinished) {
        Notification.Builder notificationBuilder = createBuilder(context)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(Notification.PRIORITY_LOW)
                .setProgress(100, currentProgress, false);

        notificationBuilder = setCategoryProgress(notificationBuilder);

        if (isFinished) {
            notificationBuilder = notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        } else {
            notificationBuilder = notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        }

        return notificationBuilder.build();
    }

    public static Notification createProgressNotification(Context context,
                                                          String title,
                                                          String description,
                                                          int currentProgress,
                                                          boolean isFinished,
                                                          String cancelAction,
                                                          PendingIntent intent) {
        Notification.Builder notificationBuilder = createBuilder(context)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(Notification.PRIORITY_LOW)
                .setProgress(100, currentProgress, false)
                .addAction(0, cancelAction, intent);

        notificationBuilder = setCategoryProgress(notificationBuilder);

        if (isFinished) {
            notificationBuilder = notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        } else {
            notificationBuilder = notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        }

        return notificationBuilder.build();
    }

    public static void createChannel(Context context) {
        final String channelName = "Unduhan";
        final String channelDescription = "Menampilkan semua informasi mengenai informasi unduhan.";

        createChannel(context, CHANNEL_ID, channelName, channelDescription);
    }

    private static Notification.Builder createBuilder(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return new Notification.Builder(context, CHANNEL_ID);
        } else {
            return new Notification.Builder(context);
        }
    }

    private static Notification.Builder setCategoryProgress(Notification.Builder builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return builder.setCategory(Notification.CATEGORY_SERVICE);
        } else {
            return builder;
        }
    }

    private static void createChannel(Context context, String id, String name, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            final NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            final NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
