package id.thony.android.quranlite.views.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import id.thony.android.quranlite.QuranApp;
import id.thony.android.quranlite.data.FontProvider;
import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.TypefaceLoader;

public class LpmqTextView extends TextView {

    private final FontProvider fontProvider;

    @SuppressLint("WrongConstant")
    public LpmqTextView(Context context) {
        super(context);

        this.fontProvider = (FontProvider) context.getSystemService(QuranApp.FONT_PROVIDER_SERVICE);

        applyTypeface();
        applyStyleBasedOnTheme();
    }

    public void applyTypeface() {
        setTypeface(TypefaceLoader.getInstance(this.fontProvider).getDefaultTypeface(), Typeface.NORMAL);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setTextColor(theme.contrastColor());
        }
    }
}
