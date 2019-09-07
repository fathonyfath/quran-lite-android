package id.fathonyfath.quranlite.views.common;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import id.fathonyfath.quranlite.utils.UnitConverter;

public class ToolbarView extends LinearLayout {

    private final TextSwitcher titleSwitcher;
    private final ViewSwitcher.ViewFactory titleTextFactory = new ViewSwitcher.ViewFactory() {
        @Override
        public View makeView() {
            TextView titleView = new LpmqTextView(getContext());
            titleView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            titleView.setGravity(Gravity.CENTER_VERTICAL);
            titleView.setPadding((int) UnitConverter.fromDpToPx(getContext(), 16f), 0, 0, 0);
            titleView.setTextSize(18f);
            return titleView;
        }
    };
    private View leftView;
    private String title;

    public ToolbarView(Context context) {
        super(context);

        this.titleSwitcher = new TextSwitcher(context);
        this.titleSwitcher.setFactory(titleTextFactory);

        initToolbarTitle();

        initConfiguration();
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
        addView(this.titleSwitcher);
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

    private void initToolbarTitle() {
        this.titleSwitcher.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_in);
        fadeIn.setDuration(250);
        fadeIn.setInterpolator(new AccelerateInterpolator());

        Animation fadeOut = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_out);
        fadeOut.setDuration(200);
        fadeOut.setInterpolator(new DecelerateInterpolator());

        this.titleSwitcher.setInAnimation(fadeIn);
        this.titleSwitcher.setOutAnimation(fadeOut);
    }

    private void updateToolbarTitle() {
        this.titleSwitcher.setText(this.title);
    }

    private void updateLeftViewConfiguration() {
        LinearLayout.LayoutParams params = new LayoutParams(this.leftView.getLayoutParams());
        params.gravity = Gravity.CENTER_VERTICAL;
        this.leftView.setLayoutParams(params);
    }
}
