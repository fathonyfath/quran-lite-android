package id.fathonyfath.quranreader.tasks;

import id.fathonyfath.quranreader.data.FontProvider;

public class HasFontInstalledTask extends BaseAsyncTask<Void, Boolean> {

    private final FontProvider fontProvider;

    public HasFontInstalledTask(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            return this.fontProvider.hasFontInstalled();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        postResult(aBoolean);
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
