package id.fathonyfath.quran.lite.useCase;

import id.fathonyfath.quran.lite.data.QuranRepository;

class DownloadAllSurahUseCase extends BaseUseCase {

    private final QuranRepository quranRepository;

    DownloadAllSurahUseCase(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
    }

    @Override
    protected void task() {

    }

    public static class Factory implements UseCaseFactory<DownloadAllSurahUseCase> {

        private final QuranRepository quranRepository;

        public Factory(QuranRepository quranRepository) {
            this.quranRepository = quranRepository;
        }

        @Override
        public DownloadAllSurahUseCase create() {
            return new DownloadAllSurahUseCase(quranRepository);
        }
    }
}
