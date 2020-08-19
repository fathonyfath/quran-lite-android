package id.fathonyfath.quran.lite.views.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;

import id.fathonyfath.quran.lite.MainActivity;
import id.fathonyfath.quran.lite.data.FontProvider;
import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.TypefaceLoader;
import id.fathonyfath.quran.lite.utils.ViewUtil;

public class LpmqEditText extends EditText {

    private final FontProvider fontProvider;

    @SuppressLint("WrongConstant")
    public LpmqEditText(Context context) {
        super(context);

        this.fontProvider = (FontProvider) context.getSystemService(MainActivity.FONT_PROVIDER_SERVICE);

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
            ViewUtil.setCursorColor(this, theme.contrastColor());
        }
    }
}
