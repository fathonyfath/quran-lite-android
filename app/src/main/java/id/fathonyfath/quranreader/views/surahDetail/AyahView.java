package id.fathonyfath.quranreader.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.fathonyfath.quranreader.utils.UnitConverter;

public class AyahView extends RelativeLayout {

    private final TextView ayahText;
    private final TextView ayahNumberText;

    public AyahView(Context context) {
        super(context);

        this.ayahText = new TextView(context);
        this.ayahNumberText = new TextView(context);

        initConfiguration();
        initView();
    }

    public void updateAyah(String ayah) {
        this.ayahText.setText(ayah);
    }

    private void initConfiguration() {
        ayahNumberText.setText("1");
        int padding = (int) UnitConverter.fromDpToPx(getContext(), 8f);
        setPadding(padding, padding, padding, padding);
    }

    private void initView() {
        int ayahPadding = (int) UnitConverter.fromDpToPx(getContext(), 12f);
        this.ayahText.setPadding(ayahPadding, ayahPadding, ayahPadding, ayahPadding);
        this.ayahText.setBackgroundColor(Color.parseColor("#f4f4f4"));
        this.ayahText.setTextSize(28f);

        this.ayahText.setLineSpacing(UnitConverter.fromDpToPx(getContext(), 30f), 1f);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "lpmq.otf");
        this.ayahText.setTypeface(typeface);

        int numberPadding = (int) UnitConverter.fromDpToPx(getContext(), 2f);
        this.ayahNumberText.setPadding(numberPadding, numberPadding, numberPadding, numberPadding);
        this.ayahNumberText.setBackgroundColor(Color.parseColor("#e5e5e5"));
        this.ayahNumberText.setTextSize(14f);

        addView(this.ayahText, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        addView(this.ayahNumberText, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }
}
