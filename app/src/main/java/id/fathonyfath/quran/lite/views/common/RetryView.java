package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.UnitConverter;
import id.fathonyfath.quran.lite.utils.ViewUtil;

public class RetryView extends LinearLayout {

    private final LpmqTextView messageText;
    private final FrameLayout retryButtonContainer;
    private final LpmqTextView retryButton;

    public RetryView(Context context) {
        super(context);

        this.messageText = new LpmqTextView(context);
        this.retryButtonContainer = new FrameLayout(context);
        this.retryButton = new LpmqTextView(context);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    public void setOnRetryClickListener(OnClickListener onRetryClickListener) {
        this.retryButton.setOnClickListener(onRetryClickListener);
    }

    private void initConfiguration() {
        setOrientation(LinearLayout.VERTICAL);
    }

    private void initView() {
        this.messageText.setTextSize(16f);

        this.retryButton.setClickable(true);
        this.retryButton.setAllCaps(true);
        this.retryButton.setMinHeight((int) UnitConverter.fromDpToPx(getContext(), 48f));
        this.retryButton.setGravity(Gravity.CENTER);
        this.retryButton.setTypeface(this.retryButton.getTypeface(), Typeface.BOLD);
        this.retryButton.setTextSize(16f);

        final int from16DpToPx = (int) UnitConverter.fromDpToPx(getContext(), 16f);
        this.retryButton.setPadding(from16DpToPx, 0, from16DpToPx, 0);

        this.messageText.setText("Terjadi masalah saat mengunduh konten.");
        this.retryButton.setText("Coba lagi");

        FrameLayout.LayoutParams retryButtonParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        this.retryButtonContainer.addView(this.retryButton, retryButtonParams);

        LinearLayout.LayoutParams firstParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams secondParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        firstParams.gravity = Gravity.CENTER_HORIZONTAL;
        secondParams.gravity = Gravity.CENTER_HORIZONTAL;
        secondParams.topMargin = (int) UnitConverter.fromDpToPx(getContext(), 8f);

        addView(this.messageText, firstParams);
        addView(this.retryButtonContainer, secondParams);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.messageText.setTextColor(theme.contrastColor());
            this.retryButton.setTextColor(theme.contrastColor());
            setRetryButtonColor(theme.contrastColor());

            ViewUtil.setDefaultSelectableBackgroundDrawable(this.retryButton, theme.contrastColor());
        }
    }

    private void setRetryButtonColor(int borderColor) {
        final GradientDrawable border = new GradientDrawable();
        border.setColor(0x00000000);
        border.setStroke((int) UnitConverter.fromDpToPx(getContext(), 2f), borderColor);
        this.retryButtonContainer.setBackground(border);
    }
}
