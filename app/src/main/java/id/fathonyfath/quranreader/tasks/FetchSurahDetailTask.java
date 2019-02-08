package id.fathonyfath.quranreader.tasks;

import id.fathonyfath.quranreader.data.OnProgressListener;
import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.models.SurahDetail;

public class FetchSurahDetailTask extends BaseAsyncTask<Surah, SurahDetail> {

    private final QuranRepository quranRepository;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            FetchSurahDetailTask.this.publishProgress(progress);
        }
    };

    public FetchSurahDetailTask(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
        this.quranRepository.setOnProgressListener(onProgressListener);
    }

    @Override
    protected SurahDetail doInBackground(Surah... surahs) {
        publishProgress(0f);
        if (surahs != null) {
            try {
                return this.quranRepository.fetchSurahDetail(surahs[0]);
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(SurahDetail s) {
        super.onPostExecute(s);

        postResult(s);
    }

    public static class Factory implements AsyncTaskFactory<FetchSurahDetailTask> {

        private final QuranRepository quranRepository;

        public Factory(QuranRepository quranRepository) {
            this.quranRepository = quranRepository;
        }

        @Override
        public FetchSurahDetailTask create() {
            return new FetchSurahDetailTask(this.quranRepository);
        }
    }
}
