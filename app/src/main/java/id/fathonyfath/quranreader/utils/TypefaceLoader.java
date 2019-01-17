package id.fathonyfath.quranreader.utils;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceLoader {

    private static TypefaceLoader instance;

    public static TypefaceLoader getInstance(Context context) {
        if (TypefaceLoader.instance == null) {
            TypefaceLoader.instance = new TypefaceLoader(context);
        }
        return TypefaceLoader.instance;
    }

    private final Typeface defaultTypeface;

    private TypefaceLoader(Context context) {
        this.defaultTypeface = Typeface.createFromAsset(context.getAssets(), "lpmq.otf");
    }

    public Typeface getDefaultTypeface() {
        return defaultTypeface;
    }
}
