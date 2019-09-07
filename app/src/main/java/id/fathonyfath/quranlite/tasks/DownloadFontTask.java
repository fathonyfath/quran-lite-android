package id.fathonyfath.quranlite.tasks;

import id.fathonyfath.quranlite.data.FontProvider;
import id.fathonyfath.quranlite.data.OnProgressListener;

public class DownloadFontTask extends BaseAsyncTask<Void, Boolean> {

    private final FontProvider fontProvider;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            DownloadFontTask.this.publishProgress(progress);
        }
    };

    public DownloadFontTask(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
        this.fontProvider.setOnProgressListener(this.onProgressListener);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        publishProgress(0f);
        try {
            return this.fontProvider.downloadFont();
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        postResult(aBoolean);
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
