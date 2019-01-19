package id.fathonyfath.quranreader.utils;

import android.graphics.Typeface;

import id.fathonyfath.quranreader.data.FontProvider;

public class TypefaceLoader {

    private static TypefaceLoader instance;

    public static TypefaceLoader getInstance(FontProvider fontProvider) {
        if (TypefaceLoader.instance == null) {
            TypefaceLoader.instance = new TypefaceLoader(fontProvider);
        }
        return TypefaceLoader.instance;
    }

    public static void invalidate() {
        TypefaceLoader.instance = null;
    }

    private Typeface defaultTypeface;

    private TypefaceLoader(FontProvider fontProvider) {
        try {
            this.defaultTypeface = Typeface.createFromFile(fontProvider.getFontFile());
        } catch (RuntimeException ignored) {
            this.defaultTypeface = Typeface.DEFAULT;
        }
    }

    public Typeface getDefaultTypeface() {
        return defaultTypeface;
    }
}
