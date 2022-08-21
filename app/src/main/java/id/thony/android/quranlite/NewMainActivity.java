package id.thony.android.quranlite;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.thony.android.quranlite.backstack.QuranBackstackHandler;
import id.thony.android.quranlite.models.DayNight;
import id.thony.android.quranlite.services.DownloaderNotification;
import id.thony.android.quranlite.services.SurahDownloaderService;
import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.themes.DayTheme;
import id.thony.android.quranlite.themes.NightTheme;
import id.thony.android.quranlite.useCase.GetDayNightUseCase;
import id.thony.android.quranlite.useCase.UseCaseCallback;
import id.thony.android.quranlite.useCase.UseCaseProvider;
import id.thony.android.quranlite.utils.DialogUtil;
import id.thony.android.quranlite.utils.LocalBroadcastManager;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.dialogManager.DialogEvent;
import id.thony.android.quranlite.utils.dialogManager.DialogEventListener;
import id.thony.android.quranlite.utils.dialogManager.DialogManager;
import id.thony.android.quranlite.view.fontDownloader.FontDownloaderKey;
import id.thony.android.quranlite.view.splash.SplashKey;
import id.thony.android.quranlite.view.splash.SplashView;
import id.thony.android.quranlite.views.ayahDetailDialog.AyahDetailDialog;
import id.thony.android.quranlite.views.noBookmarkDialog.NoBookmarkDialog;
import id.thony.android.quranlite.views.readTafsirDialog.ReadTafsirDialog;
import id.thony.android.quranlite.views.resumeBookmarkDialog.ResumeBookmarkDialog;
import id.thony.viewstack.Backstack;
import id.thony.viewstack.BackstackHandler;
import id.thony.viewstack.DefaultBackstackHandler;
import id.thony.viewstack.Navigator;

public final class NewMainActivity extends Activity implements UseCaseCallback<DayNight>, DialogEventListener {

    private final List<DialogEventListener> dialogEventListeners = new ArrayList<>();

    private FrameLayout container;
    private BaseTheme activeTheme = new DayTheme();
    private Navigator navigator;
    private QuranBackstackHandler backstackHandler;

    private Bundle pendingSavedInstanceState = null;

    private final BroadcastReceiver downloadAlreadyStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Proses pengunduhan sedang berjalan.", Toast.LENGTH_SHORT).show();
        }
    };

    private final BroadcastReceiver downloadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int failedDownload = intent.getIntExtra(SurahDownloaderService.DOWNLOAD_FAILURE_COUNT, 0);
            final String message;
            if (failedDownload > 0) {
                message = "Proses pengunduhan selesai dengan " + failedDownload + " surat gagal diunduh. Silahkan coba lagi untuk mengunduh sisanya.";
            } else {
                message = "Proses pengunduhan berhasil.";
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.pendingSavedInstanceState = savedInstanceState;

        DownloaderNotification.createChannel(this);
        registerDialogFactory();

        this.container = new FrameLayout(this);
        setContentView(this.container);

        this.backstackHandler = new QuranBackstackHandler(this, this.container);
        this.navigator = new Navigator(backstackHandler, Backstack.of(new FontDownloaderKey()));

        final GetDayNightUseCase useCase = UseCaseProvider.createUseCase(GetDayNightUseCase.class);
        if (useCase != null) {
            useCase.setCallback(this);
            useCase.run();
        } else {
            // Fallback to Light Theme
            this.activeTheme = new DayTheme();
            showViewWithActiveTheme();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        this.navigator.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(downloadAlreadyStartedReceiver,
                new IntentFilter(SurahDownloaderService.ACTION_SERVICE_ALREADY_STARTED));

        LocalBroadcastManager.getInstance(this).registerReceiver(downloadFinishedReceiver,
                new IntentFilter(SurahDownloaderService.ACTION_DOWNLOAD_FINISHED));
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadAlreadyStartedReceiver);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadFinishedReceiver);
    }

    @Override
    protected void onDestroy() {
        this.navigator.onDestroy();
        this.dialogEventListeners.clear();

        if (isFinishing()) {
            UseCaseProvider.clearAllUseCase();
        }

        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        Object service = super.getSystemService(name);
        if (service != null) {
            return service;
        }

        return getApplication().getSystemService(name);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Parcelable arguments = DialogUtil.getArguments(args);
        return DialogManager.createDialog(id, new ThemeContext(this, this.activeTheme), arguments, this);
    }

    @Override
    public void onProgress(float progress) {
        // do nothing
    }

    @Override
    public void onResult(DayNight data) {
        final GetDayNightUseCase useCase = UseCaseProvider.getUseCase(GetDayNightUseCase.class);
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
        // Fallback to Light Theme
        this.activeTheme = new DayTheme();
        showViewWithActiveTheme();
    }

    @Override
    public void onEvent(DialogEvent event, Parcelable arguments) {
        for (DialogEventListener listener : this.dialogEventListeners) {
            listener.onEvent(event, arguments);
        }
    }

    public List<DialogEventListener> getDialogEventListeners() {
        return this.dialogEventListeners;
    }

    public void relaunchActivity() {
        getWindow().setWindowAnimations(R.style.WindowAnimation);
        recreate();
    }

    private void showViewWithActiveTheme() {
        this.activeTheme = new NightTheme();
        this.backstackHandler.updateTheme(this.activeTheme);
        this.navigator.onCreate(pendingSavedInstanceState);
        pendingSavedInstanceState = null;
    }

    private void registerDialogFactory() {
        DialogManager.registerFactory(NoBookmarkDialog.class, new NoBookmarkDialog.Factory());
        DialogManager.registerFactory(ResumeBookmarkDialog.class, new ResumeBookmarkDialog.Factory());
        DialogManager.registerFactory(AyahDetailDialog.class, new AyahDetailDialog.Factory());
        DialogManager.registerFactory(ReadTafsirDialog.class, new ReadTafsirDialog.Factory());
    }

}
