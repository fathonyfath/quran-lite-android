package id.fathonyfath.quranreader.views.common;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.utils.UnitConverter;
import id.fathonyfath.quranreader.utils.ViewCallback;

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

    private final List<Runnable> removeViewRunnables;

    public WrapperView(Context context) {
        super(context);

        this.toolbarView = new ToolbarView(getContext());
        this.overlayView = new OverlayView(getContext());
        this.containerView = new FrameLayout(getContext());

        this.removeViewRunnables = new ArrayList<>();

        initConfiguration();
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        for (Runnable runnable : this.removeViewRunnables) {
            removeCallbacks(runnable);
        }
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

    public int addViewToContainer(View view) {
        this.containerView.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        view.setVisibility(View.GONE);

        return this.containerView.getChildCount() - 1;
    }

    public void showViewAtIndex(int index) {
        View selectedView = this.containerView.getChildAt(index);
        if (selectedView != null) {
            animateHideAllVisibleChild();

            Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
            fadeIn.setDuration(250);
            fadeIn.setInterpolator(new AccelerateInterpolator());

            selectedView.setVisibility(View.VISIBLE);
            selectedView.startAnimation(fadeIn);

            if (selectedView instanceof ViewCallback) {
                ((ViewCallback) selectedView).onResume();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findChildViewAtIndex(int index) {
        return (T) this.containerView.getChildAt(index);
    }

    private void animateHideAllVisibleChild() {
        for (int i = 0; i < this.containerView.getChildCount(); i++) {
            View currentView = this.containerView.getChildAt(i);
            if (currentView.getVisibility() != View.GONE) {
                Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
                fadeOut.setDuration(200);
                fadeOut.setInterpolator(new DecelerateInterpolator());

                currentView.startAnimation(fadeOut);

                Runnable runnable = new HideViewRunnable(currentView) {
                    @Override
                    public void run() {
                        this.viewToHide.setVisibility(View.GONE);
                        WrapperView.this.removeViewRunnables.remove(this);
                        removeCallbacks(this);
                    }
                };

                this.removeViewRunnables.add(runnable);
                postDelayed(runnable, 200);

                if (currentView instanceof ViewCallback) {
                    ((ViewCallback) currentView).onPause();
                }
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

    private abstract static class HideViewRunnable implements Runnable {

        protected final View viewToHide;

        private HideViewRunnable(View viewToHide) {
            this.viewToHide = viewToHide;
        }
    }
}
