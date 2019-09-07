package id.fathonyfath.quranlite.views.surahList;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.fathonyfath.quranlite.models.Surah;
import id.fathonyfath.quranlite.utils.UnitConverter;
import id.fathonyfath.quranlite.views.common.LpmqTextView;
import id.fathonyfath.quranlite.views.common.TwoLineTextView;

public class SurahView extends RelativeLayout {

    private final TwoLineTextView twoLineTextView;
    private final TextView surahNumberTextView;

    public SurahView(Context context) {
        super(context);

        this.twoLineTextView = new TwoLineTextView(getContext());
        this.surahNumberTextView = new LpmqTextView(getContext());

        initConfiguration();
        initView();
    }

    public void bindData(Surah surah) {
        String title = surah.getNameInLatin() + " - " + surah.getName();
        String subtitle = surah.getNumberOfAyah() + " Ayat";
        this.twoLineTextView.setTexts(title, subtitle);

        this.surahNumberTextView.setText(String.valueOf(surah.getNumber()));
    }

    private void initConfiguration() {
        setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 8f),
                (int) UnitConverter.fromDpToPx(getContext(), 8f),
                (int) UnitConverter.fromDpToPx(getContext(), 8f),
                (int) UnitConverter.fromDpToPx(getContext(), 8f)
        );
    }

    private void initView() {
        addView(this.twoLineTextView);
        addView(this.surahNumberTextView);

        RelativeLayout.LayoutParams twoLineParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        this.twoLineTextView.setLayoutParams(twoLineParams);

        RelativeLayout.LayoutParams surahNumberParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        surahNumberParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        surahNumberParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        this.surahNumberTextView.setLayoutParams(surahNumberParams);
        this.surahNumberTextView.setTextSize(20f);
    }


}
