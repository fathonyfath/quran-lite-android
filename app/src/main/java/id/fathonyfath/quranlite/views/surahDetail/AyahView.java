package id.fathonyfath.quranlite.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.fathonyfath.quranlite.Res;
import id.fathonyfath.quranlite.utils.UnitConverter;
import id.fathonyfath.quranlite.views.common.LpmqTextView;

public class AyahView extends RelativeLayout {

    private final TextView ayahText;
    private final TextView ayahNumberText;
    private final TextView ayahTranslationText;

    public AyahView(Context context) {
        super(context);

        this.ayahText = new LpmqTextView(context);
        this.ayahText.setId(Res.Id.surahDetailView_ayahView_ayahTextView);

        this.ayahNumberText = new LpmqTextView(context);
        this.ayahTranslationText = new LpmqTextView(context);

        initConfiguration();
        initView();
    }

    public void updateAyah(AyahDetailViewType.AyahViewModel viewModel) {
        this.ayahNumberText.setText(viewModel.ayahNumber.toString());
        this.ayahText.setText(viewModel.ayahContent);
        this.ayahTranslationText.setText(viewModel.ayahTranslation);
    }

    public void setTranslationVisibility(boolean isVisible) {
        this.ayahTranslationText.setVisibility((isVisible) ? View.VISIBLE : View.GONE);
    }

    private void initConfiguration() {
        int padding = (int) UnitConverter.fromDpToPx(getContext(), 8f);
        setPadding(padding, padding, padding, padding);
    }

    private void initView() {
        int ayahPadding = (int) UnitConverter.fromDpToPx(getContext(), 14f);
        int topBottomAyahPadding = (int) UnitConverter.fromDpToPx(getContext(), 30f);
        this.ayahText.setPadding(ayahPadding, topBottomAyahPadding, ayahPadding, topBottomAyahPadding);
        this.ayahText.setBackgroundColor(Color.parseColor("#f4f4f4"));
        this.ayahText.setTextSize(30f);

        this.ayahText.setLineSpacing(UnitConverter.fromDpToPx(getContext(), 30f), 1f);

        int leftRightNumberPadding = (int) UnitConverter.fromDpToPx(getContext(), 4f);
        this.ayahNumberText.setPadding(leftRightNumberPadding, 0, leftRightNumberPadding, 0);
        this.ayahNumberText.setBackgroundColor(Color.parseColor("#e5e5e5"));
        this.ayahNumberText.setTextSize(14f);

        this.ayahTranslationText.setTextSize(16f);
        this.ayahTranslationText.setTypeface(this.ayahTranslationText.getTypeface(), Typeface.ITALIC);

        addView(this.ayahText, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        addView(this.ayahNumberText, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        addView(this.ayahTranslationText, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LayoutParams ayahTranslationParams = (LayoutParams) this.ayahTranslationText.getLayoutParams();
        ayahTranslationParams.addRule(RelativeLayout.BELOW, this.ayahText.getId());
        ayahTranslationParams.setMargins(
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 4f),
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 4f));

        this.ayahTranslationText.setLayoutParams(ayahTranslationParams);
    }
}
