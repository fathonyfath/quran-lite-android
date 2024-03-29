package dev.fathony.android.quranlite.views.common;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import dev.fathony.android.quranlite.Res;
import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;

public class ToolbarView extends RelativeLayout {

    private final LpmqTextView titleView;
    private final LpmqEditText searchInputText;

    private View leftView;
    private Set<View> setOnRightView;
    private LinearLayout collectionRightView;

    private String title;
    private boolean isSearchMode;

    private OnSearchListener onSearchListener;

    private final TextView.OnEditorActionListener onSearchActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (onSearchListener != null) {
                    onSearchListener.onSearch(v.getText().toString());
                }
                return true;
            }
            return false;
        }
    };

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

        this.searchInputText = new LpmqEditText(getContext());
        this.searchInputText.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.searchInputText.setGravity(Gravity.CENTER_VERTICAL);
        this.searchInputText.setBackground(null);
        this.searchInputText.setTextSize(18f);
        this.searchInputText.setSingleLine();
        this.searchInputText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        this.searchInputText.setOnEditorActionListener(onSearchActionListener);

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

    public void setSearchMode(boolean searchMode) {
        this.isSearchMode = searchMode;
        updateSearchMode();
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    public EditText getSearchInput() {
        return this.searchInputText;
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
        addView(this.searchInputText);
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
        updateSearchMode();
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.toolbarColor());
            this.titleView.setTextColor(theme.contrastColor());
            this.searchInputText.setTextColor(theme.contrastColor());
        }
    }

    private void updateToolbarTitle() {
        this.titleView.setText(this.title);
    }

    private void updateSearchMode() {
        if (this.isSearchMode) {
            this.collectionRightView.setVisibility(View.GONE);
            this.titleView.setVisibility(View.GONE);
            this.searchInputText.setVisibility(View.VISIBLE);
        } else {
            this.collectionRightView.setVisibility(View.VISIBLE);
            this.titleView.setVisibility(View.VISIBLE);
            this.searchInputText.setVisibility(View.GONE);
        }
    }

    private void updateLayoutConfiguration() {
        RelativeLayout.LayoutParams titleParams = new LayoutParams(this.titleView.getLayoutParams());
        titleParams.addRule(RelativeLayout.RIGHT_OF, 0);

        RelativeLayout.LayoutParams searchParams = new LayoutParams(this.searchInputText.getLayoutParams());
        searchParams.addRule(RelativeLayout.RIGHT_OF, 0);

        if (this.leftView != null) {
            titleParams.addRule(RIGHT_OF, this.leftView.getId());
            searchParams.addRule(RIGHT_OF, this.leftView.getId());
        }

        titleParams.addRule(LEFT_OF, this.collectionRightView.getId());
        searchParams.addRule(LEFT_OF, this.collectionRightView.getId());

        this.titleView.setLayoutParams(titleParams);
        this.searchInputText.setLayoutParams(searchParams);
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

    public interface OnSearchListener {
        void onSearch(String query);
    }
}
