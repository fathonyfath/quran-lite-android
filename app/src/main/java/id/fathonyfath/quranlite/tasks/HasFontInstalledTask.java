package id.fathonyfath.quranlite.tasks;

import id.fathonyfath.quranlite.data_old.FontProviderLegacy;

public class HasFontInstalledTask extends BaseAsyncTask<Void, Boolean> {

    private final FontProviderLegacy fontProviderLegacy;

    public HasFontInstalledTask(FontProviderLegacy fontProviderLegacy) {
        this.fontProviderLegacy = fontProviderLegacy;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            return this.fontProviderLegacy.hasFontInstalled();
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

        private final FontProviderLegacy fontProviderLegacy;

        public Factory(FontProviderLegacy fontService) {
            this.fontProviderLegacy = fontService;
        }

        @Override
        public HasFontInstalledTask create() {
            return new HasFontInstalledTask(this.fontProviderLegacy);
        }
    }
}
