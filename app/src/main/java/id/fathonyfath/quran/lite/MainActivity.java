package id.fathonyfath.quran.lite;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import id.fathonyfath.quran.lite.data.BookmarkRepository;
import id.fathonyfath.quran.lite.data.ConfigRepository;
import id.fathonyfath.quran.lite.data.FontProvider;
import id.fathonyfath.quran.lite.data.QuranRepository;
import id.fathonyfath.quran.lite.data.SearchIndexRepository;
import id.fathonyfath.quran.lite.data.font.FontRemoteSource;
import id.fathonyfath.quran.lite.data.source.disk.QuranDiskSource;
import id.fathonyfath.quran.lite.data.source.disk.SearchIndexDiskSource;
import id.fathonyfath.quran.lite.data.source.network.QuranNetworkSource;
import id.fathonyfath.quran.lite.data.source.preferences.BookmarkPreferencesSource;
import id.fathonyfath.quran.lite.data.source.preferences.DayNightPreferencesSource;
import id.fathonyfath.quran.lite.models.DayNight;
import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.themes.DayTheme;
import id.fathonyfath.quran.lite.themes.NightTheme;
import id.fathonyfath.quran.lite.useCase.DoSearchUseCase;
import id.fathonyfath.quran.lite.useCase.FetchAllSurahUseCase;
import id.fathonyfath.quran.lite.useCase.FetchSurahDetailUseCase;
import id.fathonyfath.quran.lite.useCase.GetBookmarkUseCase;
import id.fathonyfath.quran.lite.useCase.GetDayNightPreferenceUseCase;
import id.fathonyfath.quran.lite.useCase.GetDayNightUseCase;
import id.fathonyfath.quran.lite.useCase.InstallFontIfNecessaryUseCase;
import id.fathonyfath.quran.lite.useCase.PutBookmarkUseCase;
import id.fathonyfath.quran.lite.useCase.PutDayNightPreferenceUseCase;
import id.fathonyfath.quran.lite.useCase.UseCaseCallback;
import id.fathonyfath.quran.lite.useCase.UseCaseProvider;
import id.fathonyfath.quran.lite.utils.DialogUtil;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.dialogManager.DialogEvent;
import id.fathonyfath.quran.lite.utils.dialogManager.DialogEventListener;
import id.fathonyfath.quran.lite.utils.dialogManager.DialogManager;
import id.fathonyfath.quran.lite.views.MainView;
import id.fathonyfath.quran.lite.views.ayahDetailDialog.AyahDetailDialog;
import id.fathonyfath.quran.lite.views.noBookmarkDialog.NoBookmarkDialog;
import id.fathonyfath.quran.lite.views.resumeBookmarkDialog.ResumeBookmarkDialog;

public class MainActivity extends Activity implements UseCaseCallback<DayNight>, DialogEventListener {

    public static final String QURAN_REPOSITORY_SERVICE = "MainActivity.QuranRepository";
    public static final String FONT_PROVIDER_SERVICE = "MainActivity.FontProvider";
    public static final String BOOKMARK_REPOSITORY_SERVICE = "MainActivity.BookmarkRepository";
    public static final String CONFIG_REPOSITORY_SERVICE = "MainActivity.ConfigRepository";
    public static final String SEARCH_INDEX_REPOSITORY_SERVICE = "MainActivity.SearchIndexRepository";
    private final List<DialogEventListener> dialogEventListeners = new ArrayList<>();
    private QuranRepository quranRepository;
    private FontProvider fontProvider;
    private BookmarkRepository bookmarkRepository;
    private ConfigRepository configRepository;
    private SearchIndexRepository searchIndexRepository;
    private MainView mainView = null;
    private BaseTheme activeTheme = new DayTheme();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initService();
        registerUseCaseFactory();
        registerDialogFactory();

        GetDayNightUseCase useCase = UseCaseProvider.createUseCase(GetDayNightUseCase.class);
        if (useCase != null) {
            useCase.setCallback(this);
            useCase.run();
        } else {
            // Fallback to Light Theme
            this.activeTheme = new DayTheme();
            showViewWithActiveTheme();
        }
    }

    public void relaunchActivity() {
        getWindow().setWindowAnimations(R.style.WindowAnimation);
        recreate();
    }

    @Override
    protected void onDestroy() {
        this.dialogEventListeners.clear();

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
        } else if (name.equals(BOOKMARK_REPOSITORY_SERVICE)) {
            return bookmarkRepository;
        } else if (name.equals(CONFIG_REPOSITORY_SERVICE)) {
            return configRepository;
        } else if (name.equals(SEARCH_INDEX_REPOSITORY_SERVICE)) {
            return searchIndexRepository;
        }
        return super.getSystemService(name);
    }

    @Override
    public void onProgress(float progress) {
        // Not used
    }

    @Override
    public void onResult(DayNight data) {
        GetDayNightUseCase useCase = UseCaseProvider.getUseCase(GetDayNightUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }

        UseCaseProvider.clearUseCase(GetDayNightUseCase.class);

        switch (data) {
            case DAY:
                this.activeTheme = new DayTheme();
                break;
            case NIGHT:
                this.activeTheme = new NightTheme();
                break;
        }

        showViewWithActiveTheme();
    }

    @Override
    public void onError(Throwable throwable) {
        // Not used
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
    }

    private void registerDialogFactory() {
        DialogManager.registerFactory(NoBookmarkDialog.class, new NoBookmarkDialog.Factory());
        DialogManager.registerFactory(ResumeBookmarkDialog.class, new ResumeBookmarkDialog.Factory());
        DialogManager.registerFactory(AyahDetailDialog.class, new AyahDetailDialog.Factory());
    }

    private void showViewWithActiveTheme() {
        this.mainView = new MainView(new ThemeContext(this, this.activeTheme));
        setContentView(MainActivity.this.mainView);
    }

    public List<DialogEventListener> getDialogEventListeners() {
        return dialogEventListeners;
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Parcelable arguments = DialogUtil.getArguments(args);
        return DialogManager.createDialog(id, new ThemeContext(this, this.activeTheme), arguments, this);
    }

    @Override
    public void onEvent(DialogEvent event, Parcelable arguments) {
        for (DialogEventListener listener : this.dialogEventListeners) {
            listener.onEvent(event, arguments);
        }
    }
}
