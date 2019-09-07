package id.fathonyfath.quranlite.tasks;

import id.fathonyfath.quranlite.data_old.FontProviderLegacy;
import id.fathonyfath.quranlite.data_old.OnProgressListener;

public class DownloadFontTask extends BaseAsyncTask<Void, Boolean> {

    private final FontProviderLegacy fontProviderLegacy;
    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onProgress(float progress) {
            DownloadFontTask.this.publishProgress(progress);
        }
    };

    public DownloadFontTask(FontProviderLegacy fontProviderLegacy) {
        this.fontProviderLegacy = fontProviderLegacy;
        this.fontProviderLegacy.setOnProgressListener(this.onProgressListener);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        publishProgress(0f);
        try {
            return this.fontProviderLegacy.downloadFont();
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

        private final FontProviderLegacy fontProviderLegacy;

        public Factory(FontProviderLegacy fontProviderLegacy) {
            this.fontProviderLegacy = fontProviderLegacy;
        }

        @Override
        public DownloadFontTask create() {
            return new DownloadFontTask(this.fontProviderLegacy);
        }
    }
}
