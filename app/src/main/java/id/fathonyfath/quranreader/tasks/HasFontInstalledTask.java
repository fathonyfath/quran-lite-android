package id.fathonyfath.quranreader.tasks;

import android.os.AsyncTask;

import id.fathonyfath.quranreader.data.FontProvider;

public class HasFontInstalledTask extends AsyncTask<Void, Float, Boolean> {

    private final FontProvider fontProvider;

    private OnTaskListener<Boolean> onTaskListener;

    public HasFontInstalledTask(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
    }

    public void setOnTaskListener(OnTaskListener<Boolean> onTaskListener) {
        this.onTaskListener = onTaskListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return this.fontProvider.hasFontInstalled();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (this.onTaskListener != null) {
            this.onTaskListener.onFinished(aBoolean);
        }
    }

    public static class Factory implements AsyncTaskFactory<HasFontInstalledTask> {

        private final FontProvider fontProvider;

        public Factory(FontProvider fontService) {
            this.fontProvider = fontService;
        }

        @Override
        public HasFontInstalledTask create() {
            return new HasFontInstalledTask(this.fontProvider);
        }
    }
}
