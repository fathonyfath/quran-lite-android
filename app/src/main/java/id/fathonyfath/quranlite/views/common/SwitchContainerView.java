package id.fathonyfath.quranlite.views.common;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import id.fathonyfath.quranlite.utils.viewLifecycle.ViewCallback;

public class SwitchContainerView extends FrameLayout {

    private final List<Runnable> removeViewRunnables;

    public SwitchContainerView(Context context) {
        super(context);

        this.removeViewRunnables = new ArrayList<>();

        this.initConfiguration();
        this.initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        for (Runnable runnable : this.removeViewRunnables) {
            removeCallbacks(runnable);
        }

        super.onDetachedFromWindow();
    }

    public int addViewToContainer(View view) {
        this.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        view.setVisibility(View.GONE);

        return this.getChildCount() - 1;
    }

    public void showViewAtIndex(int index) {
        View selectedView = this.getChildAt(index);
        if (selectedView != null) {
            this.animateHideAllVisibleChild();

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
        return (T) this.getChildAt(index);
    }

    private void animateHideAllVisibleChild() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View currentView = this.getChildAt(i);
            if (currentView.getVisibility() != View.GONE) {
                Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
                fadeOut.setDuration(200);
                fadeOut.setInterpolator(new DecelerateInterpolator());

                currentView.startAnimation(fadeOut);

                Runnable runnable = new HideViewRunnable(currentView) {
                    @Override
                    public void run() {
                        this.viewToHide.setVisibility(View.GONE);
                        SwitchContainerView.this.removeViewRunnables.remove(this);
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
        setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        setBackgroundColor(Color.WHITE);
    }

    private void initView() {

    }

    private abstract static class HideViewRunnable implements Runnable {

        protected final View viewToHide;

        private HideViewRunnable(View viewToHide) {
            this.viewToHide = viewToHide;
        }
    }
}

