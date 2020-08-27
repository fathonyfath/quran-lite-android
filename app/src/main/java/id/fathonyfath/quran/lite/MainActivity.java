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
import id.fathonyfath.quran.lite.services.DownloaderNotification;
import id.fathonyfath.quran.lite.services.SurahDownloaderService;
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

    private final List<DialogEventListener> dialogEventListeners = new ArrayList<>();

    private MainView mainView = null;
    private BaseTheme activeTheme = new DayTheme();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DownloaderNotification.createChannel(this);
        SurahDownloaderService.startForegroundService(this);

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
    public Object getSystemService(String name) {
        Object service = super.getSystemService(name);
        if (service != null) {
            return service;
        }

        return getApplication().getSystemService(name);
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
