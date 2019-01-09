package id.fathonyfath.quranreader.views.common;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.utils.UnitConverter;

public class WrapperView extends RelativeLayout {

    private final ToolbarView toolbarView;
    private final OverlayView overlayView;

    private ValueAnimator currentAnimator;

    private final ValueAnimator.AnimatorUpdateListener alphaAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            setOverlayAlpha(value);
        }
    };

    public WrapperView(Context context) {
        super(context);

        this.toolbarView = new ToolbarView(getContext());
        this.overlayView = new OverlayView(getContext());

        initConfiguration();
        initView();
    }

    public void setToolbarTitle(String title) {
        if (this.toolbarView != null) {
            this.toolbarView.setTitle(title);
        }
    }

    public void setOverlayAlpha(float alpha) {
        if (this.overlayView != null) {
            this.overlayView.setAlpha(alpha);
        }
    }

    public void animateOverlayAlpha(float alpha) {
        if (this.currentAnimator != null) {
            this.currentAnimator.removeAllListeners();
            this.currentAnimator.cancel();
        }

        if (this.overlayView != null) {
            this.currentAnimator = ValueAnimator.ofFloat(this.overlayView.getAlpha(), alpha);
            if (this.overlayView.getAlpha() > alpha) {
                this.currentAnimator.setInterpolator(new DecelerateInterpolator());
            } else {
                this.currentAnimator.setInterpolator(new AccelerateInterpolator());
            }
            this.currentAnimator.setDuration(200);
            this.currentAnimator.addUpdateListener(alphaAnimatorListener);
            this.currentAnimator.start();
        }
    }

    public void wrapView(View view) {
        removeContentView();
        addView(view);

        RelativeLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        if (this.toolbarView != null) {
            params.addRule(RelativeLayout.BELOW, this.toolbarView.getId());
        }

        view.setLayoutParams(params);
        this.overlayView.bringToFront();
    }

    public void removeContentView() {
        for (int i = 0; i < getChildCount(); i++) {
            View current = getChildAt(i);
            if (!(current instanceof ToolbarView) && !(current instanceof OverlayView)) {
                removeView(current);
            }
        }
    }

    private void initConfiguration() {
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        setBackgroundColor(Color.WHITE);
    }

    private void initView() {
        HamburgerView hamburgerView = new HamburgerView(getContext());
        this.toolbarView.setId(Res.Id.toolbar);

        this.toolbarView.setLeftView(hamburgerView);

        this.overlayView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) UnitConverter.fromDpToPx(getContext(), 8f)
        ));

        addView(this.toolbarView);
        addView(this.overlayView);

        RelativeLayout.LayoutParams toolbarParams = (LayoutParams) this.toolbarView.getLayoutParams();
        RelativeLayout.LayoutParams overlayParams = (LayoutParams) this.overlayView.getLayoutParams();

        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        this.toolbarView.setLayoutParams(toolbarParams);

        overlayParams.addRule(RelativeLayout.BELOW, toolbarView.getId());
        this.overlayView.setLayoutParams(overlayParams);
        this.overlayView.bringToFront();
    }
}
