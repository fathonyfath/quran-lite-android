package id.fathonyfath.quranlite.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;

import id.fathonyfath.quranlite.views.common.LpmqTextView;

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
}
