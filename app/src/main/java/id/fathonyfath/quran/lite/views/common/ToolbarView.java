package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import id.fathonyfath.quran.lite.Res;
import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.UnitConverter;

public class ToolbarView extends RelativeLayout {

    private final LpmqTextView titleView;

    private View leftView;
    private Set<View> setOnRightView;
    private LinearLayout collectionRightView;

    private String title;

    public ToolbarView(Context context) {
        super(context);

        this.setOnRightView = new HashSet<>();

        this.titleView = new LpmqTextView(getContext());
        this.titleView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.titleView.setGravity(Gravity.CENTER_VERTICAL);
        this.titleView.setPadding((int) UnitConverter.fromDpToPx(getContext(), 16f), 0, 0, 0);
        this.titleView.setTextSize(18f);

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        this.collectionRightView = new LinearLayout(getContext());
        this.collectionRightView.setId(Res.Id.toolbar_collectionRightView);
        this.collectionRightView.setOrientation(LinearLayout.HORIZONTAL);
        this.collectionRightView.setLayoutParams(params);

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
            this.leftView.setId(Res.Id.toolbar_leftView);
        }
        invalidate();
    }

    public void setRightView(Set<View> rightView) {
        this.setOnRightView = rightView;
        if (this.setOnRightView == null) {
            this.setOnRightView = new HashSet<>();
        } else {
            updateCollectionRightViewConfiguration();
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
        addView(this.collectionRightView);

        updateLayoutConfiguration();
    }

    private void initConfiguration() {
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) UnitConverter.fromDpToPx(getContext(), 56f)
        ));

        setBackgroundColor(Color.WHITE);

        updateToolbarTitle();
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.toolbarColor());
            this.titleView.setTextColor(theme.contrastColor());
        }
    }

    private void updateToolbarTitle() {
        this.titleView.setText(this.title);
    }

    private void updateLayoutConfiguration() {
        RelativeLayout.LayoutParams params = new LayoutParams(this.titleView.getLayoutParams());
        params.addRule(RelativeLayout.RIGHT_OF, 0);

        if (this.leftView != null) {
            params.addRule(RIGHT_OF, this.leftView.getId());
        }

        params.addRule(LEFT_OF, this.collectionRightView.getId());

        this.titleView.setLayoutParams(params);
    }

    private void updateLeftViewConfiguration() {
        RelativeLayout.LayoutParams params = new LayoutParams(this.leftView.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        this.leftView.setLayoutParams(params);
    }

    private void updateCollectionRightViewConfiguration() {
        this.collectionRightView.removeAllViews();
        for (View view : this.setOnRightView) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(view.getLayoutParams());
            params.gravity = Gravity.CENTER_VERTICAL;
            view.setLayoutParams(params);

            this.collectionRightView.addView(view);
        }
    }
}
