package dev.fathony.android.quranlite.useCase;

import dev.fathony.android.quranlite.data.FontProvider;
import dev.fathony.android.quranlite.utils.network.NetworkHelper;
import dev.fathony.android.quranlite.utils.scheduler.Schedulers;

public class InstallFontIfNecessaryUseCase extends BaseUseCase {

    private final FontProvider fontProvider;

    private NetworkHelper.CancelSignal cancellationSignal = new NetworkHelper.CancelSignal() {
        @Override
        public boolean isCancelled() {
            return InstallFontIfNecessaryUseCase.this.isCancelled();
        }
    };
    private UseCaseCallback<Boolean> callback;
    private NetworkHelper.ProgressListener progressListener = new NetworkHelper.ProgressListener() {
        @Override
        public void onProgress(int currentReadByte, int maxReadByte) {
            float progress = ((float) currentReadByte) / ((float) maxReadByte) * 100.0f;
            postProgressToMainThread(progress);
        }
    };

    public InstallFontIfNecessaryUseCase(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
    }

    @Override
    protected void task() {
        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                fetchFromProvider();
            }
        });
    }

    public void setCallback(UseCaseCallback<Boolean> callback) {
        this.callback = callback;
    }

    private void fetchFromProvider() {
        boolean fontIsInstalled = this.fontProvider.hasFontInstalled();

        if (!fontIsInstalled) {
            this.fontProvider.downloadFont(cancellationSignal, progressListener);
        }

        fontIsInstalled = this.fontProvider.hasFontInstalled();

        postResultToMainThread(fontIsInstalled);
    }

    private void postResultToMainThread(final boolean surahList) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResult(surahList);
                }
            }
        });
    }

    private void postProgressToMainThread(final float progress) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onProgress(progress);
                }
            }
        });
    }

    public static class Factory implements UseCaseFactory<InstallFontIfNecessaryUseCase> {

        private final FontProvider fontProvider;

        public Factory(FontProvider fontProvider) {
            this.fontProvider = fontProvider;
        }

        @Override
        public InstallFontIfNecessaryUseCase create() {
            return new InstallFontIfNecessaryUseCase(this.fontProvider);
        }
    }
}
