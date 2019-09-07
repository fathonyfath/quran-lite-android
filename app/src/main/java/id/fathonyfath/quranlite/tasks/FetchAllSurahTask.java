package id.fathonyfath.quranlite.tasks;

import java.util.List;

import id.fathonyfath.quranlite.data_old.OnProgressListener;
import id.fathonyfath.quranlite.data_old.QuranRepository;
import id.fathonyfath.quranlite.models.Surah;

public class FetchAllSurahTask extends BaseAsyncTask<Void, List<Surah>> {

    private final QuranRepository quranRepository;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            FetchAllSurahTask.this.publishProgress(progress);
        }
    };

    public FetchAllSurahTask(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
        this.quranRepository.setOnProgressListener(onProgressListener);
    }

    @Override
    protected List<Surah> doInBackground(Void... voids) {
        publishProgress(0f);
        try {
            return this.quranRepository.fetchAllSurah();
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

        private final QuranRepository quranRepository;


        public Factory(QuranRepository quranRepository) {
            this.quranRepository = quranRepository;
        }

        @Override
        public FetchAllSurahTask create() {
            return new FetchAllSurahTask(this.quranRepository);
        }
    }
}
