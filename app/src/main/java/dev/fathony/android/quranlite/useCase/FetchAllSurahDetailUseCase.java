package dev.fathony.android.quranlite.useCase;

import java.util.List;

import dev.fathony.android.quranlite.data.QuranRepository;
import dev.fathony.android.quranlite.models.Surah;
import dev.fathony.android.quranlite.models.SurahDetail;
import dev.fathony.android.quranlite.utils.network.NetworkHelper;
import dev.fathony.android.quranlite.utils.scheduler.Schedulers;

public class FetchAllSurahDetailUseCase extends BaseUseCase {

    private static final int RETRY_THRESHOLD = 3;

    private final QuranRepository quranRepository;

    private UseCaseCallback<Integer> callback;
    private SurahProgress surahProgressCallback;

    private int surahCount = 0;

    private int failedDownload = 0;

    private NetworkHelper.CancelSignal cancellationSignal = new NetworkHelper.CancelSignal() {
        @Override
        public boolean isCancelled() {
            return FetchAllSurahDetailUseCase.this.isCancelled();
        }
    };

    FetchAllSurahDetailUseCase(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
    }

    public void setCallback(UseCaseCallback<Integer> callback) {
        this.callback = callback;
    }

    public void setSurahProgressCallback(SurahProgress surahProgressCallback) {
        this.surahProgressCallback = surahProgressCallback;
    }

    @Override
    protected void task() {
        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                startDownloadAllSurahProcess();
            }
        });
    }

    private void startDownloadAllSurahProcess() {
        final List<Surah> surahList = quranRepository.fetchAllSurah(cancellationSignal, null);
        this.surahCount = surahList.size();

        for (int i = 0; i < surahList.size(); i++) {
            final Surah surah = surahList.get(i);
            postSurahProgressToMainThread(surah, i);

            SurahDetail currentlyDownloadedSurah = null;
            for (int j = 0; j < RETRY_THRESHOLD; j++) {
                currentlyDownloadedSurah = quranRepository.fetchSurahDetail(surah, cancellationSignal, null);
                if (currentlyDownloadedSurah != null) {
                    break;
                }
            }

            if (currentlyDownloadedSurah == null) {
                failedDownload++;
            }
        }

        postResultToMainThread(this.failedDownload);
    }

    private void postResultToMainThread(final int failedDownload) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResult(failedDownload);
                }
            }
        });
    }

    private void postSurahProgressToMainThread(final Surah currentSurah, int progress) {
        final float currentProgress = ((float) progress / (float) surahCount) * 100.0f;
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (surahProgressCallback != null) {
                    surahProgressCallback.onProgress(currentSurah, currentSurah.getNumber(), surahCount, currentProgress);
                }
            }
        });
    }

    public interface SurahProgress {
        void onProgress(Surah currentSurah, int currentSurahNumber, int maxSurahNumber, float progressPercentage);
    }

    public static class Factory implements UseCaseFactory<FetchAllSurahDetailUseCase> {

        private final QuranRepository quranRepository;

        public Factory(QuranRepository quranRepository) {
            this.quranRepository = quranRepository;
        }

        @Override
        public FetchAllSurahDetailUseCase create() {
            return new FetchAllSurahDetailUseCase(quranRepository);
        }
    }
}
