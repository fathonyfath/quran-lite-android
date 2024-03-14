package dev.fathony.android.quranlite.useCase;

import dev.fathony.android.quranlite.data.QuranRepository;
import dev.fathony.android.quranlite.models.Surah;
import dev.fathony.android.quranlite.models.SurahDetail;
import dev.fathony.android.quranlite.utils.network.NetworkHelper;
import dev.fathony.android.quranlite.utils.scheduler.Schedulers;

public class FetchSurahDetailUseCase extends BaseUseCase {

    private final QuranRepository quranRepository;

    private UseCaseCallback<SurahDetail> callback;
    private Surah surah;

    private NetworkHelper.CancelSignal cancellationSignal = new NetworkHelper.CancelSignal() {
        @Override
        public boolean isCancelled() {
            return FetchSurahDetailUseCase.this.isCancelled();
        }
    };

    private NetworkHelper.ProgressListener progressListener = new NetworkHelper.ProgressListener() {
        @Override
        public void onProgress(int currentReadByte, int maxReadByte) {
            float progress = ((float) currentReadByte) / ((float) maxReadByte) * 100.0f;
            postProgressToMainThread(progress);
        }
    };

    public FetchSurahDetailUseCase(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
    }

    @Override
    protected void task() {
        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                fetchFromRepository();
            }
        });
    }

    public void setCallback(UseCaseCallback<SurahDetail> callback) {
        this.callback = callback;
    }

    public void setArguments(Surah surah) {
        this.surah = surah;
    }

    private void fetchFromRepository() {
        if (this.surah != null) {
            final SurahDetail surahDetail = this.quranRepository.fetchSurahDetail(
                    this.surah,
                    cancellationSignal,
                    progressListener);
            postResultToMainThread(surahDetail);
        } else {
            postErrorToMainThread(new NoArgumentsException());
        }
    }

    private void postResultToMainThread(final SurahDetail surahDetail) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    if (surahDetail != null) {
                        callback.onResult(surahDetail);
                    } else {
                        callback.onError(new NoResultException());
                    }
                }
            }
        });
    }

    private void postErrorToMainThread(final Throwable throwable) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(throwable);
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

    public static class Factory implements UseCaseFactory<FetchSurahDetailUseCase> {

        private final QuranRepository quranRepository;

        public Factory(QuranRepository quranRepository) {
            this.quranRepository = quranRepository;
        }

        @Override
        public FetchSurahDetailUseCase create() {
            return new FetchSurahDetailUseCase(this.quranRepository);
        }
    }

    public static class NoResultException extends RuntimeException {
    }

    public static class NoArgumentsException extends RuntimeException {
    }
}
