package id.fathonyfath.quranreader.tasks;

import android.os.AsyncTask;

import id.fathonyfath.quranreader.data.OnProgressListener;
import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.models.SurahDetail;

public class FetchSurahDetailTask extends AsyncTask<Surah, Float, SurahDetail> {

    private final QuranRepository quranRepository;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            FetchSurahDetailTask.this.publishProgress(progress);
        }
    };

    private OnTaskListener<SurahDetail> onTaskListener;

    public FetchSurahDetailTask(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
        this.quranRepository.setOnProgressListener(onProgressListener);
    }

    public void setOnTaskListener(OnTaskListener<SurahDetail> onTaskListener) {
        this.onTaskListener = onTaskListener;
    }

    @Override
    protected SurahDetail doInBackground(Surah... surahs) {
        if (surahs != null) {
            try {
                return this.quranRepository.fetchSurahDetail(surahs[0]);
            } catch (Exception ignored) {

            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);

        if (this.onTaskListener != null) {
            this.onTaskListener.onProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(SurahDetail s) {
        super.onPostExecute(s);

        if (this.onTaskListener != null) {
            this.onTaskListener.onFinished(s);
        }
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
