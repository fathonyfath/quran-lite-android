package id.fathonyfath.quranlite.tasks;

import id.fathonyfath.quranlite.data_old.OnProgressListener;
import id.fathonyfath.quranlite.data_old.QuranRepositoryLegacy;
import id.fathonyfath.quranlite.models.Surah;
import id.fathonyfath.quranlite.models.SurahDetail;

public class FetchSurahDetailTask extends BaseAsyncTask<Surah, SurahDetail> {

    private final QuranRepositoryLegacy quranRepositoryLegacy;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            FetchSurahDetailTask.this.publishProgress(progress);
        }
    };

    public FetchSurahDetailTask(QuranRepositoryLegacy quranRepositoryLegacy) {
        this.quranRepositoryLegacy = quranRepositoryLegacy;
        this.quranRepositoryLegacy.setOnProgressListener(onProgressListener);
    }

    @Override
    protected SurahDetail doInBackground(Surah... surahs) {
        publishProgress(0f);
        if (surahs != null) {
            try {
                return this.quranRepositoryLegacy.fetchSurahDetail(surahs[0]);
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

        private final QuranRepositoryLegacy quranRepositoryLegacy;

        public Factory(QuranRepositoryLegacy quranRepositoryLegacy) {
            this.quranRepositoryLegacy = quranRepositoryLegacy;
        }

        @Override
        public FetchSurahDetailTask create() {
            return new FetchSurahDetailTask(this.quranRepositoryLegacy);
        }
    }
}
