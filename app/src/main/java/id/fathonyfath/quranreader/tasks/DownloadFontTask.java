package id.fathonyfath.quranreader.tasks;

import android.os.AsyncTask;

import id.fathonyfath.quranreader.data.FontProvider;
import id.fathonyfath.quranreader.data.OnProgressListener;

public class DownloadFontTask extends AsyncTask<Void, Float, Boolean> {

    private final FontProvider fontProvider;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            DownloadFontTask.this.publishProgress(progress);
        }
    };

    private OnTaskListener<Boolean> onTaskListener;

    public DownloadFontTask(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
        this.fontProvider.setOnProgressListener(this.onProgressListener);
    }

    public void setOnTaskListener(OnTaskListener<Boolean> onTaskListener) {
        this.onTaskListener = onTaskListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        publishProgress(0f);
        return this.fontProvider.downloadFont();
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);

        if (this.onTaskListener != null) {
            this.onTaskListener.onProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (this.onTaskListener != null) {
            this.onTaskListener.onFinished(aBoolean);
        }
    }

    public static class Factory implements AsyncTaskFactory<DownloadFontTask> {

        private final FontProvider fontProvider;

        public Factory(FontProvider fontProvider) {
            this.fontProvider = fontProvider;
        }

        @Override
        public DownloadFontTask create() {
            return new DownloadFontTask(this.fontProvider);
        }
    }
}
