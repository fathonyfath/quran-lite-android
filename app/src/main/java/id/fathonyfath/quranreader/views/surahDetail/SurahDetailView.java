package id.fathonyfath.quranreader.views.surahDetail;

import android.content.Context;
import android.os.Parcelable;
import android.widget.FrameLayout;
import android.widget.TextView;

import id.fathonyfath.quranreader.models.Surah;

public class SurahDetailView extends FrameLayout {

    private final Surah selectedSurah;

    public SurahDetailView(Context context, Surah selectedSurah) {
        super(context);

        this.selectedSurah = selectedSurah;

        initView();
    }

    private void initView() {
        TextView textView = new TextView(getContext());
        textView.setText(this.selectedSurah.getName());
        addView(textView);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }
}
