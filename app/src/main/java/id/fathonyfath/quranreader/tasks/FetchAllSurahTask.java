package id.fathonyfath.quranreader.tasks;

import android.os.AsyncTask;

import java.util.List;

import id.fathonyfath.quranreader.data.OnProgressListener;
import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.models.Surah;

public class FetchAllSurahTask extends AsyncTask<Void, Float, List<Surah>> {

    private final QuranRepository quranRepository;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            FetchAllSurahTask.this.publishProgress(progress);
        }
    };

    private OnTaskListener<List<Surah>> onTaskListener;

    public FetchAllSurahTask(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
        this.quranRepository.setOnProgressListener(onProgressListener);
    }

    public void setOnTaskListener(OnTaskListener<List<Surah>> onTaskListener) {
        this.onTaskListener = onTaskListener;
    }

    @Override
    protected List<Surah> doInBackground(Void... voids) {
        publishProgress(0f);
        return this.quranRepository.fetchAllSurah();
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);

        if (this.onTaskListener != null) {
            this.onTaskListener.onProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(List<Surah> s) {
        super.onPostExecute(s);

        if (this.onTaskListener != null) {
            this.onTaskListener.onFinished(s);
        }
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
