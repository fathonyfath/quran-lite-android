package id.fathonyfath.quranlite.tasks;

import java.util.List;

import id.fathonyfath.quranlite.data_old.OnProgressListener;
import id.fathonyfath.quranlite.data_old.QuranRepositoryLegacy;
import id.fathonyfath.quranlite.models.Surah;

public class FetchAllSurahTask extends BaseAsyncTask<Void, List<Surah>> {

    private final QuranRepositoryLegacy quranRepositoryLegacy;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            FetchAllSurahTask.this.publishProgress(progress);
        }
    };

    public FetchAllSurahTask(QuranRepositoryLegacy quranRepositoryLegacy) {
        this.quranRepositoryLegacy = quranRepositoryLegacy;
        this.quranRepositoryLegacy.setOnProgressListener(onProgressListener);
    }

    @Override
    protected List<Surah> doInBackground(Void... voids) {
        publishProgress(0f);
        try {
            return this.quranRepositoryLegacy.fetchAllSurah();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Surah> s) {
        super.onPostExecute(s);
        postResult(s);
    }

    public static class Factory implements AsyncTaskFactory<FetchAllSurahTask> {

        private final QuranRepositoryLegacy quranRepositoryLegacy;


        public Factory(QuranRepositoryLegacy quranRepositoryLegacy) {
            this.quranRepositoryLegacy = quranRepositoryLegacy;
        }

        @Override
        public FetchAllSurahTask create() {
            return new FetchAllSurahTask(this.quranRepositoryLegacy);
        }
    }
}
