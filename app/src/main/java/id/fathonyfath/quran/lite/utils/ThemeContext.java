package id.fathonyfath.quran.lite.utils;

import android.content.Context;
import android.content.ContextWrapper;

import id.fathonyfath.quran.lite.themes.BaseTheme;

public class ThemeContext extends ContextWrapper {

    private final BaseTheme theme;

    public ThemeContext(Context base, BaseTheme theme) {
        super(base);
        this.theme = theme;
    }

    public static BaseTheme saveUnwrapTheme(Context context) {
        if (context instanceof ThemeContext) {
            ThemeContext themeContext = (ThemeContext) context;
            return themeContext.theme;
        }

        return null;
    }
}
