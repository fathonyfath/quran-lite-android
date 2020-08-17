package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

public class ElevationView extends View {

    public ElevationView(Context context) {
        super(context);

        initOverlayColor();
    }

    @SuppressWarnings("deprecation")
    private void initOverlayColor() {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                Color.BLACK,
                Color.TRANSPARENT
        });

        setBackground(drawable);
    }
}
