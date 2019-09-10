package id.fathonyfath.quranlite.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import id.fathonyfath.quranlite.themes.BaseTheme;
import id.fathonyfath.quranlite.utils.ThemeContext;
import id.fathonyfath.quranlite.utils.UnitConverter;
import id.fathonyfath.quranlite.views.common.LpmqTextView;

public class BasmalahView extends FrameLayout {

    private final TextView basmalahText;
    private final String basmalahContent = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ";

    public BasmalahView(Context context) {
        super(context);

        this.basmalahText = new LpmqTextView(context);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    private void initConfiguration() {
        int padding = (int) UnitConverter.fromDpToPx(getContext(), 8f);
        setPadding(padding, padding, padding, padding);
    }

    private void initView() {
        int basmalahPadding = (int) UnitConverter.fromDpToPx(getContext(), 14f);
        int topBottomBasmalahPadding = (int) UnitConverter.fromDpToPx(getContext(), 28f);
        this.basmalahText.setPadding(basmalahPadding, topBottomBasmalahPadding, basmalahPadding, topBottomBasmalahPadding);
        this.basmalahText.setBackgroundColor(Color.parseColor("#f4f4f4"));
        this.basmalahText.setTextSize(30f);

        this.basmalahText.setLineSpacing(UnitConverter.fromDpToPx(getContext(), 30f), 1f);

        this.basmalahText.setText(this.basmalahContent);

        addView(this.basmalahText, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.primary());
            this.basmalahText.setBackgroundColor(theme.primaryDark());
        }
    }
}
