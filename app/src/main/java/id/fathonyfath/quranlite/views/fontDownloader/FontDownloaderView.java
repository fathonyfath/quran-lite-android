package id.fathonyfath.quranlite.views.fontDownloader;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.fathonyfath.quranlite.tasks.AsyncTaskProvider;
import id.fathonyfath.quranlite.tasks.DownloadFontTask;
import id.fathonyfath.quranlite.tasks.HasFontInstalledTask;
import id.fathonyfath.quranlite.tasks.OnTaskListener;
import id.fathonyfath.quranlite.utils.UnitConverter;
import id.fathonyfath.quranlite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quranlite.views.common.ProgressView;

public class FontDownloaderView extends FrameLayout implements ViewCallback {

    private final LinearLayout containerLayout;
    private final TextView informationTextView;
    private final ProgressView progressView;

    private final OnTaskListener<Boolean> hasFontInstalledCallback = new OnTaskListener<Boolean>() {
        @Override
        public void onProgress(float progress) {

        }

        @Override
        public void onFinished(Boolean result) {
            if (result) {
                FontDownloaderView.this.notifyDownloadComplete();
            } else {
                FontDownloaderView.this.runDownloadFontTask();
            }

            clearHasFontInstalledTask();
        }
    };

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

    public FontDownloaderView(Context context) {
        super(context);

        this.containerLayout = new LinearLayout(context);
        this.informationTextView = new TextView(context);
        this.progressView = new ProgressView(context);

        initConfiguration();
        initView();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {
        registerTaskCallbacks();
        runHasFontInstalledTask();
    }

    @Override
    public void onPause() {
        clearTaskCallbacks();
    }

    @Override
    public void onStop() {
        clearHasFontInstalledTask();
        clearDownloadFontTask();
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

    private void runHasFontInstalledTask() {
        final HasFontInstalledTask task = AsyncTaskProvider.getAsyncTask(HasFontInstalledTask.class);

        if (task.getStatus() == AsyncTask.Status.PENDING) {
            task.setOnTaskListener(this.hasFontInstalledCallback);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void runDownloadFontTask() {
        final DownloadFontTask task = AsyncTaskProvider.getAsyncTask(DownloadFontTask.class);

        if (task.getStatus() == AsyncTask.Status.PENDING) {
            task.setOnTaskListener(this.downloadFontCallback);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void clearHasFontInstalledTask() {
        AsyncTaskProvider.clearAsyncTask(HasFontInstalledTask.class);
    }

    private void clearDownloadFontTask() {
        AsyncTaskProvider.clearAsyncTask(DownloadFontTask.class);
    }

    private void registerTaskCallbacks() {
        AsyncTaskProvider.getAsyncTask(HasFontInstalledTask.class).setOnTaskListener(this.hasFontInstalledCallback);
        AsyncTaskProvider.getAsyncTask(DownloadFontTask.class).setOnTaskListener(this.downloadFontCallback);
    }

    private void clearTaskCallbacks() {
        AsyncTaskProvider.getAsyncTask(HasFontInstalledTask.class).setOnTaskListener(null);
        AsyncTaskProvider.getAsyncTask(DownloadFontTask.class).setOnTaskListener(null);
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
