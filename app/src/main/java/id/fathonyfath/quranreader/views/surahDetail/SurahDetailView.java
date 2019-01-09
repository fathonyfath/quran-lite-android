package id.fathonyfath.quranreader.views.surahDetail;

import android.content.Context;
import android.os.Parcelable;
import android.widget.FrameLayout;
import android.widget.TextView;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.utils.ViewCallback;

public class SurahDetailView extends FrameLayout implements ViewCallback {

    private final TextView textView;

    public SurahDetailView(Context context) {
        super(context);
        setId(Res.Id.surahDetailView);

        this.textView = new TextView(getContext());
        this.textView.setId(Res.Id.surahDetailView_textView);

        initView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    public void updateView(Surah selectedSurah) {
        this.textView.setText(selectedSurah.getName());
    }

    private void initView() {
        addView(this.textView);
    }
}
