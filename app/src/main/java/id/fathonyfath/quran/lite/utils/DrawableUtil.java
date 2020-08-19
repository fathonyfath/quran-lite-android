package id.fathonyfath.quran.lite.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.StateSet;

public class DrawableUtil {

    public static Drawable getStateListDrawable(int selectedColor) {
        final StateListDrawable drawable = new StateListDrawable();

        final Drawable transparentColor = new ColorDrawable().mutate();
        transparentColor.setAlpha(0);

        long color = selectedColor | 0x00000000ff000000;

        ColorDrawable greyColor = new ColorDrawable();
        greyColor.setColor((int) color);

        drawable.addState(new int[]{android.R.attr.state_pressed}, greyColor);
        drawable.addState(StateSet.WILD_CARD, transparentColor);

        return drawable;
    }
}
