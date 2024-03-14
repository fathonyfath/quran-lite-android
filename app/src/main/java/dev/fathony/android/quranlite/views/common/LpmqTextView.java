package dev.fathony.android.quranlite.views.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import dev.fathony.android.quranlite.QuranApp;
import dev.fathony.android.quranlite.data.FontProvider;
import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.TypefaceLoader;

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
