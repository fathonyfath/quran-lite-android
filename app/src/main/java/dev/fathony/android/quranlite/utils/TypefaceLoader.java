package dev.fathony.android.quranlite.utils;

import android.graphics.Typeface;

import dev.fathony.android.quranlite.data.FontProvider;

public class TypefaceLoader {

    private static TypefaceLoader instance;
    private Typeface defaultTypeface;

    private TypefaceLoader(FontProvider fontProvider) {
        try {
            this.defaultTypeface = Typeface.createFromFile(fontProvider.getFontFile());
        } catch (RuntimeException ignored) {
            this.defaultTypeface = Typeface.DEFAULT;
        }
    }

    public static TypefaceLoader getInstance(FontProvider fontProvider) {
        if (TypefaceLoader.instance == null) {
            TypefaceLoader.instance = new TypefaceLoader(fontProvider);
        }
        return TypefaceLoader.instance;
    }

    public static void invalidate() {
        TypefaceLoader.instance = null;
    }

    public Typeface getDefaultTypeface() {
        return defaultTypeface;
    }
}
