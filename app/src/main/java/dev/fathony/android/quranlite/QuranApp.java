package dev.fathony.android.quranlite;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dev.fathony.android.quranlite.data.BookmarkRepository;
import dev.fathony.android.quranlite.data.ConfigRepository;
import dev.fathony.android.quranlite.data.FontProvider;
import dev.fathony.android.quranlite.data.QuranRepository;
import dev.fathony.android.quranlite.data.SearchIndexRepository;
import dev.fathony.android.quranlite.data.font.FontRemoteSource;
import dev.fathony.android.quranlite.data.source.disk.QuranDiskSource;
import dev.fathony.android.quranlite.data.source.disk.SearchIndexDiskSource;
import dev.fathony.android.quranlite.data.source.network.QuranNetworkSource;
import dev.fathony.android.quranlite.data.source.preferences.BookmarkPreferencesSource;
import dev.fathony.android.quranlite.data.source.preferences.DayNightPreferencesSource;
import dev.fathony.android.quranlite.useCase.DoSearchUseCase;
import dev.fathony.android.quranlite.useCase.FetchAllSurahDetailUseCase;
import dev.fathony.android.quranlite.useCase.FetchAllSurahUseCase;
import dev.fathony.android.quranlite.useCase.FetchSurahDetailUseCase;
import dev.fathony.android.quranlite.useCase.GetBookmarkUseCase;
import dev.fathony.android.quranlite.useCase.GetDayNightPreferenceUseCase;
import dev.fathony.android.quranlite.useCase.GetDayNightUseCase;
import dev.fathony.android.quranlite.useCase.InstallFontIfNecessaryUseCase;
import dev.fathony.android.quranlite.useCase.PutBookmarkUseCase;
import dev.fathony.android.quranlite.useCase.PutDayNightPreferenceUseCase;
import dev.fathony.android.quranlite.useCase.UseCaseProvider;

public class QuranApp extends Application {

    public static final String QURAN_REPOSITORY_SERVICE = "MainActivity.QuranRepository";
    public static final String FONT_PROVIDER_SERVICE = "MainActivity.FontProvider";
    public static final String BOOKMARK_REPOSITORY_SERVICE = "MainActivity.BookmarkRepository";
    public static final String CONFIG_REPOSITORY_SERVICE = "MainActivity.ConfigRepository";
    public static final String SEARCH_INDEX_REPOSITORY_SERVICE = "MainActivity.SearchIndexRepository";

    private QuranRepository quranRepository;
    private FontProvider fontProvider;
    private BookmarkRepository bookmarkRepository;
    private ConfigRepository configRepository;
    private SearchIndexRepository searchIndexRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        initService();
        registerUseCaseFactory();
    }

    @Override
    public Object getSystemService(String name) {
        switch (name) {
            case QURAN_REPOSITORY_SERVICE:
                return quranRepository;
            case FONT_PROVIDER_SERVICE:
                return fontProvider;
            case BOOKMARK_REPOSITORY_SERVICE:
                return bookmarkRepository;
            case CONFIG_REPOSITORY_SERVICE:
                return configRepository;
            case SEARCH_INDEX_REPOSITORY_SERVICE:
                return searchIndexRepository;
        }
        return super.getSystemService(name);
    }

    private void initService() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        QuranDiskSource quranDiskSource = new QuranDiskSource(this.getApplicationContext());
        QuranNetworkSource quranNetworkSource = new QuranNetworkSource();
        FontRemoteSource fontRemoteSource = new FontRemoteSource();
        BookmarkPreferencesSource bookmarkPreferencesSource = new BookmarkPreferencesSource(defaultSharedPreferences);
        DayNightPreferencesSource dayNightPreferencesSource = new DayNightPreferencesSource(defaultSharedPreferences);
        SearchIndexDiskSource searchIndexDiskSource = new SearchIndexDiskSource(this.getApplicationContext());

        this.quranRepository = new QuranRepository(quranDiskSource, quranNetworkSource);
        this.fontProvider = new FontProvider(this.getApplicationContext(), fontRemoteSource);
        this.bookmarkRepository = new BookmarkRepository(bookmarkPreferencesSource);
        this.configRepository = new ConfigRepository(dayNightPreferencesSource);
        this.searchIndexRepository = new SearchIndexRepository(searchIndexDiskSource);
    }

    private void registerUseCaseFactory() {
        UseCaseProvider.registerFactory(InstallFontIfNecessaryUseCase.class, new InstallFontIfNecessaryUseCase.Factory(this.fontProvider));
        UseCaseProvider.registerFactory(FetchAllSurahUseCase.class, new FetchAllSurahUseCase.Factory(this.quranRepository));
        UseCaseProvider.registerFactory(FetchSurahDetailUseCase.class, new FetchSurahDetailUseCase.Factory(this.quranRepository));
        UseCaseProvider.registerFactory(GetBookmarkUseCase.class, new GetBookmarkUseCase.Factory(this.bookmarkRepository));
        UseCaseProvider.registerFactory(PutBookmarkUseCase.class, new PutBookmarkUseCase.Factory(this.bookmarkRepository));
        UseCaseProvider.registerFactory(GetDayNightUseCase.class, new GetDayNightUseCase.Factory(this, this.configRepository));
        UseCaseProvider.registerFactory(GetDayNightPreferenceUseCase.class, new GetDayNightPreferenceUseCase.Factory(this.configRepository));
        UseCaseProvider.registerFactory(PutDayNightPreferenceUseCase.class, new PutDayNightPreferenceUseCase.Factory(this, this.configRepository));
        UseCaseProvider.registerFactory(DoSearchUseCase.class, new DoSearchUseCase.Factory(this.quranRepository, this.searchIndexRepository));
        UseCaseProvider.registerFactory(FetchAllSurahDetailUseCase.class, new FetchAllSurahDetailUseCase.Factory(this.quranRepository));
    }
}
