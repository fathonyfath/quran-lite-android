package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;

public class TwoLineTextView extends LinearLayout {

    private final TextView firstLineTextView;
    private final TextView secondLineTextView;

    public TwoLineTextView(Context context) {
        super(context);
        this.firstLineTextView = new LpmqTextView(context);
        this.secondLineTextView = new LpmqTextView(context);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    public void setTexts(String first, String second) {
        this.firstLineTextView.setText(first);
        this.secondLineTextView.setText(second);
    }

    private void initConfiguration() {
        setOrientation(LinearLayout.VERTICAL);
    }

    private void initView() {
        addView(this.firstLineTextView);
        addView(this.secondLineTextView);

        this.firstLineTextView.setTextSize(24f);
        this.secondLineTextView.setTextSize(16f);

        LinearLayout.LayoutParams firstParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams secondParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        this.firstLineTextView.setLayoutParams(firstParams);
        this.secondLineTextView.setLayoutParams(secondParams);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.firstLineTextView.setTextColor(theme.contrastColor());
            this.secondLineTextView.setTextColor(theme.contrastColor());
        }
    }
}
