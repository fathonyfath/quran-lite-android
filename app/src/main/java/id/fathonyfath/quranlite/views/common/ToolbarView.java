package id.fathonyfath.quranlite.views.common;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import id.fathonyfath.quranlite.themes.BaseTheme;
import id.fathonyfath.quranlite.utils.ThemeContext;
import id.fathonyfath.quranlite.utils.UnitConverter;

public class ToolbarView extends LinearLayout {

    private final LpmqTextView titleView;

    private View leftView;

    private String title;

    public ToolbarView(Context context) {
        super(context);

        this.titleView = new LpmqTextView(getContext());
        this.titleView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.titleView.setGravity(Gravity.CENTER_VERTICAL);
        this.titleView.setPadding((int) UnitConverter.fromDpToPx(getContext(), 16f), 0, 0, 0);
        this.titleView.setTextSize(18f);

        initConfiguration();
        applyStyleBasedOnTheme();
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        updateToolbarTitle();
    }

    public void setLeftView(View leftView) {
        this.leftView = leftView;
        if (this.leftView != null) {
            updateLeftViewConfiguration();
        }
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        removeAllViews();
        if (this.leftView != null) {
            addView(this.leftView);
        }
        addView(this.titleView);
    }

    private void initConfiguration() {
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) UnitConverter.fromDpToPx(getContext(), 56f)
        ));

        setOrientation(LinearLayout.HORIZONTAL);

        setBackgroundColor(Color.WHITE);

        updateToolbarTitle();
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.primary());
            this.titleView.setTextColor(theme.objectOnPrimary());
        }
    }

    private void updateToolbarTitle() {
        this.titleView.setText(this.title);
    }

    private void updateLeftViewConfiguration() {
        LinearLayout.LayoutParams params = new LayoutParams(this.leftView.getLayoutParams());
        params.gravity = Gravity.CENTER_VERTICAL;
        this.leftView.setLayoutParams(params);
    }
}
