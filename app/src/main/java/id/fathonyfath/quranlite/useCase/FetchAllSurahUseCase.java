package id.fathonyfath.quranlite.useCase;

import java.util.List;

import id.fathonyfath.quranlite.data.QuranRepository;
import id.fathonyfath.quranlite.models.Surah;
import id.fathonyfath.quranlite.utils.network.NetworkHelper;
import id.fathonyfath.quranlite.utils.scheduler.Schedulers;

public class FetchAllSurahUseCase extends BaseUseCase {

    private final QuranRepository quranRepository;

    private UseCaseCallback<List<Surah>> callback;

    private NetworkHelper.CancelSignal cancellationSignal = new NetworkHelper.CancelSignal() {
        @Override
        public boolean isCancelled() {
            return FetchAllSurahUseCase.this.isCancelled();
        }
    };

    private NetworkHelper.ProgressListener progressListener = new NetworkHelper.ProgressListener() {
        @Override
        public void onProgress(int currentReadByte, int maxReadByte) {
            float progress = ((float) currentReadByte) / ((float) maxReadByte) * 100.0f;
            postProgressToMainThread(progress);
        }
    };

    public FetchAllSurahUseCase(QuranRepository quranRepository) {
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

    public void setCallback(UseCaseCallback<List<Surah>> callback) {
        this.callback = callback;
    }

    private void fetchFromRepository() {
        final List<Surah> surahList = this.quranRepository.fetchAllSurah(cancellationSignal, progressListener);
        postResultToMainThread(surahList);
    }

    private void postResultToMainThread(final List<Surah> surahList) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    if (surahList != null) {
                        callback.onResult(surahList);
                    } else {
                        callback.onError(new NoResultException());
                    }
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

    public static class Factory implements UseCaseFactory<FetchAllSurahUseCase> {

        private final QuranRepository quranRepository;

        public Factory(QuranRepository quranRepository) {
            this.quranRepository = quranRepository;
        }

        @Override
        public FetchAllSurahUseCase create() {
            return new FetchAllSurahUseCase(this.quranRepository);
        }
    }

    public static class NoResultException extends RuntimeException {
    }
}
