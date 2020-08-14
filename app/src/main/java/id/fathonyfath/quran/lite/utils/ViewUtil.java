package id.fathonyfath.quran.lite.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import id.fathonyfath.quran.lite.MainActivity;
import id.fathonyfath.quran.lite.views.common.LpmqTextView;

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
