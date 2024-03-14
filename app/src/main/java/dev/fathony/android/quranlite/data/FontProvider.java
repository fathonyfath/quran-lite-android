package dev.fathony.android.quranlite.data;

import android.content.Context;

import java.io.File;

public class FontProvider {

    private static final String FONT_FILENAME = "font.otf";

    private final File fontFile;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public FontProvider(Context context) {
        File fontLocation = new File(context.getFilesDir(), "fonts");
        fontLocation.mkdirs();

        this.fontFile = new File(fontLocation, FONT_FILENAME);
    }

    public boolean downloadFont() {
        return false;
    }

    public boolean hasFontInstalled() {
        return this.fontFile.exists();
    }

    public File getFontFile() {
        return this.fontFile;
    }

    public boolean deleteFontFile() {
        return this.fontFile.delete();
    }
}
