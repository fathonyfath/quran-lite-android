package id.fathonyfath.quranreader.views.common;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import id.fathonyfath.quranreader.utils.TypefaceLoader;

public class LpmqTextView extends TextView {

    public LpmqTextView(Context context) {
        super(context);
        applyTypeface();
    }

    private void applyTypeface() {
        setTypeface(TypefaceLoader.getInstance(getContext()).getDefaultTypeface(), Typeface.NORMAL);
    }
}
