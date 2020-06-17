package id.fathonyfath.quran.lite;

import android.app.Activity;
import android.os.Bundle;

import id.fathonyfath.quran.lite.data.FontProvider;
import id.fathonyfath.quran.lite.data.QuranRepository;
import id.fathonyfath.quran.lite.data.font.FontRemoteSource;
import id.fathonyfath.quran.lite.data.source.disk.QuranDiskSource;
import id.fathonyfath.quran.lite.data.source.network.QuranNetworkSource;
import id.fathonyfath.quran.lite.themes.DarkTheme;
import id.fathonyfath.quran.lite.themes.LightTheme;
import id.fathonyfath.quran.lite.useCase.FetchAllSurahUseCase;
import id.fathonyfath.quran.lite.useCase.FetchSurahDetailUseCase;
import id.fathonyfath.quran.lite.useCase.InstallFontIfNecessaryUseCase;
import id.fathonyfath.quran.lite.useCase.UseCaseProvider;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.views.MainView;

public class MainActivity extends Activity {

    public static final String QURAN_REPOSITORY_SERVICE = "MainActivity.QuranRepository";
    public static final String FONT_PROVIDER_SERVICE = "MainActivity.FontProvider";

    private QuranRepository quranRepository;
    private FontProvider fontProvider;

    private MainView mainView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initService();
        registerUseCaseFactory();

        this.mainView = new MainView(new ThemeContext(this, new LightTheme()));
        setContentView(this.mainView);
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            UseCaseProvider.clearAllUseCase();
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
        QuranDiskSource quranDiskSource = new QuranDiskSource(this.getApplicationContext());
        QuranNetworkSource quranNetworkSource = new QuranNetworkSource();
        FontRemoteSource fontRemoteSource = new FontRemoteSource();

        this.quranRepository = new QuranRepository(quranDiskSource, quranNetworkSource);
        this.fontProvider = new FontProvider(this.getApplicationContext(), fontRemoteSource);
    }

    private void registerUseCaseFactory() {
        UseCaseProvider.registerFactory(InstallFontIfNecessaryUseCase.class, new InstallFontIfNecessaryUseCase.Factory(this.fontProvider));
        UseCaseProvider.registerFactory(FetchAllSurahUseCase.class, new FetchAllSurahUseCase.Factory(this.quranRepository));
        UseCaseProvider.registerFactory(FetchSurahDetailUseCase.class, new FetchSurahDetailUseCase.Factory(this.quranRepository));
    }
}
