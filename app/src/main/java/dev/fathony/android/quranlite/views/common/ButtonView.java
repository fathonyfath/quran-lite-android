package dev.fathony.android.quranlite.views.common;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;
import dev.fathony.android.quranlite.utils.ViewUtil;

public class ButtonView extends FrameLayout {

    private final LpmqTextView retryButton;

    public ButtonView(Context context) {
        super(context);

        this.retryButton = new LpmqTextView(context);

        initView();
        applyStyleBasedOnTheme();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.retryButton.setOnClickListener(l);
    }

    public void setText(String text) {
        this.retryButton.setText(text);
    }

    private void initView() {
        this.retryButton.setClickable(true);
        this.retryButton.setAllCaps(true);
        this.retryButton.setMinHeight((int) UnitConverter.fromDpToPx(getContext(), 48f));
        this.retryButton.setGravity(Gravity.CENTER);
        this.retryButton.setTypeface(this.retryButton.getTypeface(), Typeface.BOLD);
        this.retryButton.setTextSize(16f);

        final int from16DpToPx = (int) UnitConverter.fromDpToPx(getContext(), 16f);
        this.retryButton.setPadding(from16DpToPx, 0, from16DpToPx, 0);

        FrameLayout.LayoutParams retryButtonParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        this.addView(this.retryButton, retryButtonParams);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.retryButton.setTextColor(theme.contrastColor());
            setRetryButtonColor(theme.contrastColor());

            ViewUtil.setDefaultSelectableBackgroundDrawable(this.retryButton, theme.contrastColor());
        }
    }

    private void setRetryButtonColor(int borderColor) {
        final GradientDrawable border = new GradientDrawable();
        border.setColor(0x00000000);
        border.setStroke((int) UnitConverter.fromDpToPx(getContext(), 2f), borderColor);
        this.setBackground(border);
    }
}
