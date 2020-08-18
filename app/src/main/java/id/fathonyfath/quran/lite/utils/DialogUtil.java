package id.fathonyfath.quran.lite.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import id.fathonyfath.quran.lite.MainActivity;
import id.fathonyfath.quran.lite.utils.dialogManager.Dialog;
import id.fathonyfath.quran.lite.utils.dialogManager.DialogEventListener;

public class DialogUtil {

    private static String ARGUMENTS_KEY = "ARGUMENTS";

    private static Bundle createBundle(Parcelable arguments) {
        final Bundle bundle = new Bundle();
        if (arguments != null) {
            bundle.putParcelable(ARGUMENTS_KEY, arguments);
        }
        return bundle;
    }

    public static Parcelable getArguments(Bundle bundle) {
        if (bundle.containsKey(ARGUMENTS_KEY)) {
            return bundle.getParcelable(ARGUMENTS_KEY);
        }
        return null;
    }

    public static void showDialog(View view, Class<? extends Dialog> dialogClass, Parcelable arguments) {
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
            activity.showDialog(dialogClass.hashCode(), createBundle(arguments));
        }
    }

    public static void addListener(View view, DialogEventListener listener) {
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
            activity.getDialogEventListeners().add(listener);
        }
    }

    public static void removeListener(View view, DialogEventListener listener) {
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
            activity.getDialogEventListeners().remove(listener);
        }
    }
}
