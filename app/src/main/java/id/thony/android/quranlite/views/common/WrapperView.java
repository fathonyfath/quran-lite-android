package id.thony.android.quranlite.views.common;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.Set;

import id.thony.android.quranlite.Res;
import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.UnitConverter;

public abstract class WrapperView extends RelativeLayout {

    private final ToolbarView toolbarView;
    private final ElevationView elevationView;
    private final FrameLayout contentView;

    public WrapperView(Context context) {
        super(context);

        this.toolbarView = new ToolbarView(context);
        this.elevationView = new ElevationView(context);
        this.contentView = new FrameLayout(context);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    protected void setToolbarTitle(String title) {
        if (this.toolbarView != null) {
            this.toolbarView.setTitle(title);
        }
    }

    protected void setElevationAlpha(float alpha) {
        if (this.elevationView != null) {
            this.elevationView.setAlpha(alpha);
        }
    }

    protected void setToolbarIsSearchMode(boolean isSearchMode) {
        if (this.toolbarView != null) {
            this.toolbarView.setSearchMode(isSearchMode);
        }
    }

    protected void setOnSearchListener(ToolbarView.OnSearchListener listener) {
        if (this.toolbarView != null) {
            this.toolbarView.setOnSearchListener(listener);
        }
    }

    protected void setToolbarLeftView(View view) {
        if (this.toolbarView != null) {
            this.toolbarView.setLeftView(view);
        }
    }

    protected void setToolbarRightViews(Set<View> viewSet) {
        if (this.toolbarView != null) {
            this.toolbarView.setRightView(viewSet);
        }
    }

    protected EditText getSearchInput() {
        if (this.toolbarView != null) {
            return this.toolbarView.getSearchInput();
        }
        return null;
    }

    @Override
    public void addView(View child) {
        this.contentView.addView(child);
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

        this.elevationView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) UnitConverter.fromDpToPx(getContext(), 8f)
        ));

        super.addView(this.toolbarView);
        super.addView(this.elevationView);
        super.addView(this.contentView);

        final RelativeLayout.LayoutParams toolbarParams = (LayoutParams) this.toolbarView.getLayoutParams();
        final RelativeLayout.LayoutParams overlayParams = (LayoutParams) this.elevationView.getLayoutParams();
        final RelativeLayout.LayoutParams containerParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        this.toolbarView.setLayoutParams(toolbarParams);

        overlayParams.addRule(RelativeLayout.BELOW, toolbarView.getId());
        this.elevationView.setLayoutParams(overlayParams);
        this.elevationView.bringToFront();

        containerParams.addRule(BELOW, this.toolbarView.getId());
        this.contentView.setLayoutParams(containerParams);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.baseColor());
        }
    }
}
