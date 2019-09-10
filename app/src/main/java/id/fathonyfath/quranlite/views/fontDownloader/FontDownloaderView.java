package id.fathonyfath.quranlite.views.fontDownloader;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.fathonyfath.quranlite.themes.BaseTheme;
import id.fathonyfath.quranlite.useCase.InstallFontIfNecessaryUseCase;
import id.fathonyfath.quranlite.useCase.UseCaseCallback;
import id.fathonyfath.quranlite.useCase.UseCaseProvider;
import id.fathonyfath.quranlite.utils.ThemeContext;
import id.fathonyfath.quranlite.utils.UnitConverter;
import id.fathonyfath.quranlite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quranlite.views.common.ProgressView;

public class FontDownloaderView extends FrameLayout implements ViewCallback {

    private final LinearLayout containerLayout;
    private final TextView informationTextView;
    private final ProgressView progressView;
    private OnViewEventListener onViewEventListener;
    private final UseCaseCallback<Boolean> installFontIfNecessaryCallback = new UseCaseCallback<Boolean>() {
        @Override
        public void onProgress(float progress) {
            updateTextProgress(progress);
        }

        @Override
        public void onResult(Boolean data) {
            // Don't forget to cleanup
            unregisterUseCaseCallback();
            clearUseCase();

            if (data) {
                FontDownloaderView.this.notifyDownloadComplete();
            } else {
                FontDownloaderView.this.notifyDownloadFailed();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            // Don't forget to cleanup
            unregisterUseCaseCallback();
            clearUseCase();

            FontDownloaderView.this.notifyDownloadFailed();
        }
    };

    public FontDownloaderView(Context context) {
        super(context);

        this.containerLayout = new LinearLayout(context);
        this.informationTextView = new TextView(context);
        this.progressView = new ProgressView(context);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    @Override
    public void onStart() {
        this.progressView.setVisibility(View.VISIBLE);
        updateTextProgress(0f);
    }

    @Override
    public void onResume() {
        if (!tryToRestoreUseCase()) {
            createAndRunUseCase();
        }
    }

    @Override
    public void onPause() {
        unregisterUseCaseCallback();
    }

    @Override
    public void onStop() {
        clearUseCase();
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

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.primary());
            this.informationTextView.setTextColor(theme.objectOnPrimary());
        }
    }

    private boolean tryToRestoreUseCase() {
        InstallFontIfNecessaryUseCase useCase = UseCaseProvider.getUseCase(InstallFontIfNecessaryUseCase.class);
        if (useCase != null) {
            useCase.setCallback(this.installFontIfNecessaryCallback);
            return true;
        }
        return false;
    }

    private void createAndRunUseCase() {
        InstallFontIfNecessaryUseCase useCase = UseCaseProvider.createUseCase(InstallFontIfNecessaryUseCase.class);
        useCase.setCallback(this.installFontIfNecessaryCallback);
        useCase.run();
    }

    private void unregisterUseCaseCallback() {
        InstallFontIfNecessaryUseCase useCase = UseCaseProvider.getUseCase(InstallFontIfNecessaryUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }
    }

    private void clearUseCase() {
        UseCaseProvider.clearUseCase(InstallFontIfNecessaryUseCase.class);
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
