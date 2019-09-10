package id.fathonyfath.quranlite.utils;

import android.content.Context;
import android.content.ContextWrapper;

import id.fathonyfath.quranlite.themes.BaseTheme;

public class ThemeContext extends ContextWrapper {

    public static BaseTheme saveUnwrapTheme(Context context) {
        if (context instanceof ThemeContext) {
            ThemeContext themeContext = (ThemeContext) context;
            return themeContext.theme;
        }

        return null;
    }

    private final BaseTheme theme;

    public ThemeContext(Context base, BaseTheme theme) {
        super(base);
        this.theme = theme;
    }
}
