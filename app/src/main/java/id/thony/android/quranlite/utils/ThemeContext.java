package id.thony.android.quranlite.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.ContextThemeWrapper;

import id.thony.android.quranlite.themes.BaseTheme;

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
        } else if (context instanceof ContextThemeWrapper) {
            Context baseContext = ((ContextThemeWrapper) context).getBaseContext();
            if (baseContext != null) {
                return saveUnwrapTheme(baseContext);
            }
        }

        return null;
    }
}
