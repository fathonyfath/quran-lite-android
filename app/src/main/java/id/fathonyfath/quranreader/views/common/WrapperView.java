package id.fathonyfath.quranreader.views.common;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.utils.UnitConverter;

public class WrapperView extends RelativeLayout {

    private final ToolbarView toolbarView;
    private final OverlayView overlayView;
    private final FrameLayout containerView;

    private ValueAnimator currentAnimator;

    private final ValueAnimator.AnimatorUpdateListener alphaAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            setOverlayAlpha(value);
        }
    };

    private Runnable removeViewRunnable = null;

    public WrapperView(Context context) {
        super(context);

        this.toolbarView = new ToolbarView(getContext());
        this.overlayView = new OverlayView(getContext());
        this.containerView = new FrameLayout(getContext());

        initConfiguration();
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.removeViewRunnable);
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

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        fadeIn.setDuration(250);
        fadeIn.setInterpolator(new AccelerateInterpolator());

        view.startAnimation(fadeIn);
    }

    public void removeContentView() {
        removeCallbacks(this.removeViewRunnable);

        for (int i = 0; i < getChildCount(); i++) {
            View current = getChildAt(i);
            if (!(current instanceof ToolbarView) && !(current instanceof OverlayView)) {
                Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
                fadeOut.setDuration(200);
                fadeOut.setInterpolator(new DecelerateInterpolator());
                current.startAnimation(fadeOut);

                this.removeViewRunnable = new RemoveViewAtIndexRunnable(i) {
                    @Override
                    public void run() {
                        removeViewAt(this.indexViewToRemove);
                    }
                };

                postDelayed(this.removeViewRunnable, 200);
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
        this.toolbarView.setId(Res.Id.toolbar);

        HamburgerView hamburgerView = new HamburgerView(getContext());
        this.toolbarView.setLeftView(hamburgerView);

        this.overlayView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) UnitConverter.fromDpToPx(getContext(), 8f)
        ));

        this.containerView.setId(Res.Id.containerView);

        addView(this.toolbarView);
        addView(this.overlayView);
        addView(this.containerView);

        final RelativeLayout.LayoutParams toolbarParams = (LayoutParams) this.toolbarView.getLayoutParams();
        final RelativeLayout.LayoutParams overlayParams = (LayoutParams) this.overlayView.getLayoutParams();
        final RelativeLayout.LayoutParams containerParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        this.toolbarView.setLayoutParams(toolbarParams);

        overlayParams.addRule(RelativeLayout.BELOW, toolbarView.getId());
        this.overlayView.setLayoutParams(overlayParams);
        this.overlayView.bringToFront();

        containerParams.addRule(BELOW, this.toolbarView.getId());
        this.containerView.setLayoutParams(containerParams);
    }

    private abstract static class RemoveViewAtIndexRunnable implements Runnable {

        protected final int indexViewToRemove;

        private RemoveViewAtIndexRunnable(int indexViewToRemove) {
            this.indexViewToRemove = indexViewToRemove;
        }
    }
}
