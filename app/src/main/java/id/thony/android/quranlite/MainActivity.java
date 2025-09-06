package id.thony.android.quranlite;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.WindowInsets;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import java.util.ArrayList;
import java.util.List;

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
import id.thony.android.quranlite.views.MainView;
import id.thony.android.quranlite.views.ayahDetailDialog.AyahDetailDialog;
import id.thony.android.quranlite.views.noBookmarkDialog.NoBookmarkDialog;
import id.thony.android.quranlite.views.readTafsirDialog.ReadTafsirDialog;
import id.thony.android.quranlite.views.requestNotificationPermissionDialog.ExplainNotificationPermissionDialog;
import id.thony.android.quranlite.views.resumeBookmarkDialog.ResumeBookmarkDialog;

public class MainActivity extends Activity implements UseCaseCallback<DayNight>, DialogEventListener {

    private final List<DialogEventListener> dialogEventListeners = new ArrayList<>();

    private MainView mainView = null;
    private BaseTheme activeTheme = new DayTheme();
    private OnBackInvokedCallback onBackInvokedCallback = null;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedCallback = () -> MainActivity.this.mainView.onBackPressed();
        }

        DownloaderNotification.createChannel(this);

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 301) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Proses pengunduhan dimulai. Cek notifikasi untuk mengetahui perkembangan proses unduh.", Toast.LENGTH_SHORT).show();
                SurahDownloaderService.startService(this);
            }
        }
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

    /**
     * Suppressed for below API level 33 compatibility.
     */
    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (!this.mainView.onBackPressed()) {
                super.onBackPressed();
            }
        } else {
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

    @SuppressLint("InlinedApi")
    public void requestNotificationPermission() {
        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 301);
    }

    private void registerDialogFactory() {
        DialogManager.registerFactory(NoBookmarkDialog.class, new NoBookmarkDialog.Factory());
        DialogManager.registerFactory(ResumeBookmarkDialog.class, new ResumeBookmarkDialog.Factory());
        DialogManager.registerFactory(AyahDetailDialog.class, new AyahDetailDialog.Factory());
        DialogManager.registerFactory(ReadTafsirDialog.class, new ReadTafsirDialog.Factory());
        DialogManager.registerFactory(ExplainNotificationPermissionDialog.class, new ExplainNotificationPermissionDialog.Factory());
    }

    private void showViewWithActiveTheme() {
        this.mainView = new MainView(new ThemeContext(this, this.activeTheme));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.mainView.setStackEventListener((event, size) -> {
                if (size > 1) {
                    getOnBackInvokedDispatcher().registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, onBackInvokedCallback);
                } else {
                    getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(onBackInvokedCallback);
                }
            });
        }
        setWindowInsetsPadding();

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

    private void setWindowInsetsPadding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mainView.setOnApplyWindowInsetsListener((view, windowInsets) -> {
                final int top;
                final int bottom;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    final Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
                    top = insets.top;
                    bottom = insets.bottom;
                } else {
                    top = windowInsets.getSystemWindowInsetTop();
                    bottom = windowInsets.getSystemWindowInsetBottom();
                }
                this.mainView.setPadding(0, top, 0, bottom);
                return windowInsets;
            });
        }
    }
}
