package id.fathonyfath.quranreader.views.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import id.fathonyfath.quranreader.MainActivity;
import id.fathonyfath.quranreader.data.FontProvider;
import id.fathonyfath.quranreader.utils.TypefaceLoader;

public class LpmqTextView extends TextView {

    private final FontProvider fontProvider;

    @SuppressLint("WrongConstant")
    public LpmqTextView(Context context) {
        super(context);

        this.fontProvider = (FontProvider) context.getSystemService(MainActivity.FONT_PROVIDER_SERVICE);

        applyTypeface();
    }

    public void applyTypeface() {
        setTypeface(TypefaceLoader.getInstance(this.fontProvider).getDefaultTypeface(), Typeface.NORMAL);
    }
}
