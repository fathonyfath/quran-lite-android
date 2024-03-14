package dev.fathony.android.quranlite.views.searchSurah;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.fathony.android.quranlite.Res;
import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;
import dev.fathony.android.quranlite.views.common.LpmqTextView;

public class NoResultView extends LinearLayout {

    private final TextView emojiText;
    private final LpmqTextView informationText;

    public NoResultView(Context context) {
        super(context);

        this.emojiText = new TextView(context);
        this.informationText = new LpmqTextView(context);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();

        setId(Res.Id.searchSurahView_noResultView);
    }

    private void initConfiguration() {
        setOrientation(LinearLayout.VERTICAL);
    }

    private void initView() {
        this.emojiText.setText(":(");
        this.emojiText.setTextSize(52.0f);

        this.informationText.setTextSize(16f);
        this.informationText.setGravity(Gravity.CENTER_HORIZONTAL);
        this.informationText.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0
        );

        this.informationText.setText("Tidak ada hasil dari kata kunci anda, silahkan coba kata kunci lain.");

        LinearLayout.LayoutParams firstParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams secondParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        firstParams.gravity = Gravity.CENTER_HORIZONTAL;
        secondParams.gravity = Gravity.CENTER_HORIZONTAL;

        secondParams.topMargin = (int) UnitConverter.fromDpToPx(getContext(), 14.0f);

        addView(this.emojiText, firstParams);
        addView(this.informationText, secondParams);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.emojiText.setTextColor(theme.contrastColor());
        }
    }
}
