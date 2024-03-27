package id.thony.android.quranlite.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import id.thony.android.quranlite.MainActivity;
import id.thony.android.quranlite.views.common.LpmqTextView;

public class ViewUtil {

    public static void onBackPressed(View view) {
        Activity hostingActivity = null;
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                hostingActivity = (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        if (hostingActivity != null) {
            hostingActivity.onBackPressed();
        }
    }

    public static void recreateActivity(View view) {
        Activity hostingActivity = null;
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                hostingActivity = (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        if (hostingActivity instanceof MainActivity) {
            MainActivity activity = (MainActivity) hostingActivity;
            activity.relaunchActivity();
        }
    }

    public static void requestNotificationPermission(View view) {
        Activity hostingActivity = null;
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                hostingActivity = (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        if (hostingActivity instanceof MainActivity) {
            MainActivity activity = (MainActivity) hostingActivity;
            activity.requestNotificationPermission();
        }
    }

    public static boolean hasNotificationPermission(View view) {
        NotificationManager notificationManager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return notificationManager.areNotificationsEnabled();
        } else {
            return true;
        }
    }

    public static void reloadChildsTypeface(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                reloadChildsTypeface(viewGroup.getChildAt(i));
            }
        } else if (view instanceof LpmqTextView) {
            LpmqTextView textView = (LpmqTextView) view;
            textView.applyTypeface();
        }
    }

    public static void setDefaultSelectableBackgroundDrawable(View view, int selectedColor) {
        view.setBackground(DrawableUtil.getStateListDrawable(selectedColor));
        view.getBackground().mutate().setAlpha(20);
    }

    public static void setDefaultSelectableBackgroundDrawable(ListView view, int selectedColor) {
        view.setSelector(DrawableUtil.getStateListDrawable(selectedColor));
        view.getSelector().mutate().setAlpha(20);
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

}
