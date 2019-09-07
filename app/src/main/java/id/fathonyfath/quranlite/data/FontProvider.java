package id.fathonyfath.quranlite.data;

import android.content.Context;

import java.io.File;

import id.fathonyfath.quranlite.data.remote.FontService;
import id.fathonyfath.quranlite.data.remote.OnDownloadProgressListener;

public class FontProvider {

    private static final String FONT_FILENAME = "font.otf";

    private final FontService fontService;
    private final OnDownloadProgressListener onDownloadProgressListener = new OnDownloadProgressListener() {
        @Override
        public void onDownloadProgress(int currentProgress, int maxProgress) {
            if (FontProvider.this.onProgressListener != null) {
                float progress = ((float) currentProgress) / ((float) maxProgress) * 100.0f;
                FontProvider.this.onProgressListener.onProgress(progress);
            }
        }
    };

    private final File fontFile;

    private OnProgressListener onProgressListener;

    public FontProvider(Context context, FontService fontService) {
        this.fontService = fontService;
        this.fontService.setOnDownloadProgressListener(this.onDownloadProgressListener);

        File fontLocation = new File(context.getFilesDir(), "fonts");
        fontLocation.mkdirs();

        this.fontFile = new File(fontLocation, FONT_FILENAME);
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public boolean downloadFont() {
        return this.fontService.downloadFont(this.fontFile);
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
