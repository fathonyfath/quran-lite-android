package id.fathonyfath.quran.lite.utils;

import android.content.Context;

public class UnitConverter {

    private UnitConverter() {

    }

    public static float fromPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float fromDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float fromPxToSp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static float fromSpToPx(Context context, float sp) {
        return sp * context.getResources().getDisplayMetrics().scaledDensity;
    }
}
