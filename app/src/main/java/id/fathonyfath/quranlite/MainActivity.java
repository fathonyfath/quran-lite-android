package id.fathonyfath.quranlite;

import android.app.Activity;
import android.os.Bundle;

import id.fathonyfath.quranlite.data_old.FontProvider;
import id.fathonyfath.quranlite.data_old.QuranRepository;
import id.fathonyfath.quranlite.data_old.disk.QuranDiskService;
import id.fathonyfath.quranlite.data_old.remote.FontService;
import id.fathonyfath.quranlite.data_old.remote.QuranJsonService;
import id.fathonyfath.quranlite.tasks.AsyncTaskProvider;
import id.fathonyfath.quranlite.tasks.DownloadFontTask;
import id.fathonyfath.quranlite.tasks.FetchAllSurahTask;
import id.fathonyfath.quranlite.tasks.FetchSurahDetailTask;
import id.fathonyfath.quranlite.tasks.HasFontInstalledTask;
import id.fathonyfath.quranlite.views.MainView;

public class MainActivity extends Activity {

    public static final String QURAN_REPOSITORY_SERVICE = "MainActivity.QuranRepository";
    public static final String FONT_PROVIDER_SERVICE = "MainActivity.FontProvider";

    private QuranDiskService quranDiskService;
    private QuranJsonService quranJsonService;
    private FontService fontService;

    private QuranRepository quranRepository;
    private FontProvider fontProvider;

    private MainView mainView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initService();
        registerAsyncTaskFactory();

        this.mainView = new MainView(this);
        setContentView(this.mainView);
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            AsyncTaskProvider.clearAllAsyncTask();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!this.mainView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if (name.equals(QURAN_REPOSITORY_SERVICE)) {
            return quranRepository;
        } else if (name.equals(FONT_PROVIDER_SERVICE)) {
            return fontProvider;
        }
        return super.getSystemService(name);
    }

    private void initService() {
        this.quranDiskService = new QuranDiskService(this.getApplicationContext());
        this.quranJsonService = new QuranJsonService(this.quranDiskService);
        this.fontService = new FontService();

        this.quranRepository = new QuranRepository(this.quranJsonService, this.quranDiskService);
        this.fontProvider = new FontProvider(this.getApplicationContext(), this.fontService);
    }

    private void registerAsyncTaskFactory() {
        AsyncTaskProvider.registerFactory(DownloadFontTask.class, new DownloadFontTask.Factory(this.fontProvider));
        AsyncTaskProvider.registerFactory(HasFontInstalledTask.class, new HasFontInstalledTask.Factory(this.fontProvider));
        AsyncTaskProvider.registerFactory(FetchAllSurahTask.class, new FetchAllSurahTask.Factory(this.quranRepository));
        AsyncTaskProvider.registerFactory(FetchSurahDetailTask.class, new FetchSurahDetailTask.Factory(this.quranRepository));
    }
}
