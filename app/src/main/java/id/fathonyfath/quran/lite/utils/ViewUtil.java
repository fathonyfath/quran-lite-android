package id.fathonyfath.quran.lite.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;

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

    public static void setCursorColor(EditText editText, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            final GradientDrawable drawable = new GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    new int[]{color, color}
            );
            drawable.setSize(
                    (int) UnitConverter.fromSpToPx(editText.getContext(), 2.0f),
                    (int) editText.getTextSize()
            );
            editText.setTextCursorDrawable(drawable);
        } else {
            setCursorColorReflection(editText, color);
        }
    }

    private static void setCursorColorReflection(EditText editText, int color) {
        try {
            Field editorField;
            Field cursorDrawableField;
            Field cursorDrawableResourceField;

            cursorDrawableResourceField = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorDrawableResourceField.setAccessible(true);
            final Class<?> drawableFieldClass;
            editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            drawableFieldClass = editorField.getType();
            cursorDrawableField = drawableFieldClass.getDeclaredField("mCursorDrawable");
            cursorDrawableField.setAccessible(true);

            final Drawable drawable = DrawableUtil.getDrawable(editText.getContext(),
                    cursorDrawableResourceField.getInt(editText));
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            cursorDrawableField.set(editorField.get(editText), new Drawable[]{drawable, drawable});
        } catch (Exception ignored) {

        }
    }

}
