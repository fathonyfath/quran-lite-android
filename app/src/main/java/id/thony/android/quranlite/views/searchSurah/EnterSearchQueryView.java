package id.thony.android.quranlite.views.searchSurah;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import id.thony.android.quranlite.Res;
import id.thony.android.quranlite.utils.UnitConverter;
import id.thony.android.quranlite.views.common.LpmqTextView;
import id.thony.android.quranlite.views.common.SearchView;

public class EnterSearchQueryView extends LinearLayout {

    private final SearchView searchView;
    private final LpmqTextView informationText;

    public EnterSearchQueryView(Context context) {
        super(context);

        this.searchView = new SearchView(context);
        this.informationText = new LpmqTextView(context);

        initConfiguration();
        initView();

        setId(Res.Id.searchSurahView_enterSearchQueryView);
    }

    private void initConfiguration() {
        setOrientation(LinearLayout.VERTICAL);
    }

    private void initView() {
        this.searchView.setClickable(false);

        this.informationText.setTextSize(16f);

        this.informationText.setText("Masukkan kata kunci untuk pencarian anda.");
        this.informationText.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams firstParams = new LayoutParams(
                (int) UnitConverter.fromDpToPx(getContext(), 84.0f),
                (int) UnitConverter.fromDpToPx(getContext(), 84.0f)
        );

        LinearLayout.LayoutParams secondParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        firstParams.gravity = Gravity.CENTER_HORIZONTAL;
        secondParams.gravity = Gravity.CENTER_HORIZONTAL;

        addView(this.searchView, firstParams);
        addView(this.informationText, secondParams);
    }
}
