package id.thony.android.quranlite.data;

import android.content.Context;

import java.io.File;

import id.thony.android.quranlite.data.font.FontRemoteSource;
import id.thony.android.quranlite.utils.network.NetworkHelper;

public class FontProvider {

    private static final String FONT_FILENAME = "font.otf";

    private final File fontFile;
    private final FontRemoteSource fontRemoteSource;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public FontProvider(Context context, FontRemoteSource fontRemoteSource) {
        this.fontRemoteSource = fontRemoteSource;

        File fontLocation = new File(context.getFilesDir(), "fonts");
        fontLocation.mkdirs();

        this.fontFile = new File(fontLocation, FONT_FILENAME);
    }

    public boolean downloadFont(NetworkHelper.CancelSignal cancelSignal,
                                NetworkHelper.ProgressListener progressListener) {
        return this.fontRemoteSource.downloadFont(this.fontFile, cancelSignal, progressListener);
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
