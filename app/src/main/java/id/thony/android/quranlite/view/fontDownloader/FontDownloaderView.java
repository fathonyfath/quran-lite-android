package id.thony.android.quranlite.view.fontDownloader;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.thony.android.quranlite.Res;
import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.UnitConverter;
import id.thony.android.quranlite.views.common.ProgressView;

public class FontDownloaderView extends FrameLayout {

    private final LinearLayout containerLayout;
    private final TextView informationTextView;
    private final ProgressView progressView;

    public FontDownloaderView(Context context) {
        super(context);

        setId(Res.Id.fontDownloaderView);

        this.containerLayout = new LinearLayout(context);
        this.informationTextView = new TextView(context);
        this.progressView = new ProgressView(context);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
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
        this.progressView.setId(Res.Id.fontDownloaderView_progressView);

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
            this.setBackgroundColor(theme.baseColor());
            this.informationTextView.setTextColor(theme.contrastColor());
        }
    }
}
