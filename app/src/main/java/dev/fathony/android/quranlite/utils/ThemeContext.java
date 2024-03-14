package dev.fathony.android.quranlite.utils;

import android.content.Context;
import android.content.ContextWrapper;

import dev.fathony.android.quranlite.themes.BaseTheme;

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
        } else if (context instanceof ContextWrapper) {
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            if (baseContext != null) {
                return saveUnwrapTheme(baseContext);
            }
        }

        return null;
    }
}
