package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.UnitConverter;

public class RetryView extends LinearLayout {

    private final LpmqTextView messageText;
    private final ButtonView retryButton;

    public RetryView(Context context) {
        super(context);

        this.messageText = new LpmqTextView(context);
        this.retryButton = new ButtonView(context);

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

        this.messageText.setText("Terjadi masalah saat mengunduh konten.");
        this.retryButton.setText("Coba lagi");

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
        addView(this.retryButton, secondParams);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.messageText.setTextColor(theme.contrastColor());
        }
    }
}
