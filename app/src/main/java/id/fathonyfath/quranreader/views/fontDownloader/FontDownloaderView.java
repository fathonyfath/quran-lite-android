package id.fathonyfath.quranreader.views.fontDownloader;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.fathonyfath.quranreader.tasks.AsyncTaskHolder;
import id.fathonyfath.quranreader.tasks.DownloadFontTask;
import id.fathonyfath.quranreader.tasks.HasFontInstalledTask;
import id.fathonyfath.quranreader.tasks.OnTaskListener;
import id.fathonyfath.quranreader.utils.UnitConverter;
import id.fathonyfath.quranreader.utils.ViewCallback;
import id.fathonyfath.quranreader.views.common.ProgressView;

public class FontDownloaderView extends FrameLayout implements ViewCallback {

    private final LinearLayout containerLayout;
    private final TextView informationTextView;
    private final ProgressView progressView;

    private final HasFontInstalledTask.Factory hasFontInstalledTaskFactory;
    private final OnTaskListener<Boolean> hasFontInstalledCallback = new OnTaskListener<Boolean>() {
        @Override
        public void onProgress(float progress) {

        }

        @Override
        public void onFinished(Boolean result) {
            if (result) {
                FontDownloaderView.this.notifyDownloadComplete();
            } else {
                FontDownloaderView.this.downloadFont();
            }

            clearHasFontInstalledTask();
        }
    };

    private final DownloadFontTask.Factory downloadFontTaskFactory;
    private final OnTaskListener<Boolean> downloadFontCallback = new OnTaskListener<Boolean>() {
        @Override
        public void onProgress(float progress) {
            updateTextProgress(progress);
        }

        @Override
        public void onFinished(Boolean result) {
            if (result) {
                FontDownloaderView.this.notifyDownloadComplete();
            } else {
                FontDownloaderView.this.notifyDownloadFailed();
            }

            clearDownloadFontTask();
        }
    };

    private OnViewEventListener onViewEventListener;

    public FontDownloaderView(Context context,
                              HasFontInstalledTask.Factory hasFontInstalledTaskFactory,
                              DownloadFontTask.Factory downloadFontTaskFactory) {
        super(context);

        this.containerLayout = new LinearLayout(context);
        this.informationTextView = new TextView(context);
        this.progressView = new ProgressView(context);
        this.hasFontInstalledTaskFactory = hasFontInstalledTaskFactory;
        this.downloadFontTaskFactory = downloadFontTaskFactory;

        initConfiguration();
        initView();
    }

    @Override
    public void onResume() {
        registerTaskCallbacks();
        checkIfHasFontInstalled();
    }

    @Override
    public void onPause() {
        clearTaskCallbacks();
    }

    public void setOnViewEventListener(OnViewEventListener onViewEventListener) {
        this.onViewEventListener = onViewEventListener;
    }

    private void initConfiguration() {
        setBackgroundColor(Color.WHITE);

        this.containerLayout.setOrientation(LinearLayout.VERTICAL);
    }

    private void initView() {
        this.informationTextView.setText("Mohon menunggu. Sedang mengunduh font untuk tampilan huruf arab...");
        this.informationTextView.setTextSize(16f);
        this.informationTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        this.containerLayout.addView(this.informationTextView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        final LinearLayout.LayoutParams informationParams = (LinearLayout.LayoutParams) this.informationTextView.getLayoutParams();
        informationParams.gravity = Gravity.CENTER_HORIZONTAL;
        this.informationTextView.setLayoutParams(informationParams);

        this.containerLayout.addView(this.progressView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        final LinearLayout.LayoutParams progressParams = (LinearLayout.LayoutParams) this.progressView.getLayoutParams();
        progressParams.gravity = Gravity.CENTER_HORIZONTAL;
        progressParams.setMargins(
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 12f),
                0,
                0
        );
        this.progressView.setLayoutParams(progressParams);

        this.containerLayout.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 32f),
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 32f),
                0
        );
        addView(this.containerLayout, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        final LayoutParams containerParams = (LayoutParams) this.containerLayout.getLayoutParams();
        containerParams.gravity = Gravity.CENTER;
        this.containerLayout.setLayoutParams(containerParams);
    }

    private void checkIfHasFontInstalled() {
        if (AsyncTaskHolder.hasFontInstalledTaskInstance == null) {
            AsyncTaskHolder.hasFontInstalledTaskInstance = this.hasFontInstalledTaskFactory.create();
            AsyncTaskHolder.hasFontInstalledTaskInstance.setOnTaskListener(this.hasFontInstalledCallback);
            AsyncTaskHolder.hasFontInstalledTaskInstance.execute();
        }
    }

    private void downloadFont() {
        if (AsyncTaskHolder.downloadFontTaskInstance == null) {
            AsyncTaskHolder.downloadFontTaskInstance = this.downloadFontTaskFactory.create();
            AsyncTaskHolder.downloadFontTaskInstance.setOnTaskListener(this.downloadFontCallback);
            AsyncTaskHolder.downloadFontTaskInstance.execute();
        }
    }

    private void clearHasFontInstalledTask() {
        if (AsyncTaskHolder.hasFontInstalledTaskInstance != null) {
            AsyncTaskHolder.hasFontInstalledTaskInstance.setOnTaskListener(null);
            AsyncTaskHolder.hasFontInstalledTaskInstance = null;

        }
    }

    private void clearDownloadFontTask() {
        if (AsyncTaskHolder.downloadFontTaskInstance != null) {
            AsyncTaskHolder.downloadFontTaskInstance.setOnTaskListener(null);
            AsyncTaskHolder.downloadFontTaskInstance = null;
        }
    }

    private void registerTaskCallbacks() {
        if (AsyncTaskHolder.hasFontInstalledTaskInstance != null) {
            AsyncTaskHolder.hasFontInstalledTaskInstance.setOnTaskListener(this.hasFontInstalledCallback);
        }

        if (AsyncTaskHolder.downloadFontTaskInstance != null) {
            AsyncTaskHolder.downloadFontTaskInstance.setOnTaskListener(this.downloadFontCallback);
        }
    }

    private void clearTaskCallbacks() {
        if (AsyncTaskHolder.hasFontInstalledTaskInstance != null) {
            AsyncTaskHolder.hasFontInstalledTaskInstance.setOnTaskListener(null);
        }

        if (AsyncTaskHolder.downloadFontTaskInstance != null) {
            AsyncTaskHolder.downloadFontTaskInstance.setOnTaskListener(null);
        }
    }

    private void updateTextProgress(float progress) {
        this.progressView.updateProgress(progress);
    }

    private void notifyDownloadComplete() {
        if (this.onViewEventListener != null) {
            this.onViewEventListener.onDownloadCompleted();
        }
    }

    private void notifyDownloadFailed() {
        if (this.onViewEventListener != null) {
            this.onViewEventListener.onDownloadFailed();
        }
    }

    public interface OnViewEventListener {
        void onDownloadCompleted();

        void onDownloadFailed();
    }
}
