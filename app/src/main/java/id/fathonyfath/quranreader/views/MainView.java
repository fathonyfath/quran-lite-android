package id.fathonyfath.quranreader.views;

import android.annotation.SuppressLint;
import android.content.Context;

import id.fathonyfath.quranreader.MainActivity;
import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.tasks.FetchAllSurahTask;
import id.fathonyfath.quranreader.views.common.WrapperView;
import id.fathonyfath.quranreader.views.surahDetail.SurahDetailView;
import id.fathonyfath.quranreader.views.surahList.SurahListView;

public class MainView extends WrapperView {

    private boolean isToolbarFlying = false;

    @SuppressLint("WrongConstant")
    private final QuranRepository quranRepository = (QuranRepository) getContext()
            .getSystemService(MainActivity.QURAN_REPOSITORY_SERVICE);

    private final SurahListView.OnViewEventListener surahListEventListener = new SurahListView.OnViewEventListener() {
        @Override
        public void onSurahListScroll(float scrollY) {
            if (scrollY > 0f) {
                updateIsToolbarFlying(true);
            } else {
                updateIsToolbarFlying(false);
            }
        }

        @Override
        public void onSurahSelected(Surah selectedSurah) {
            navigateToSurahDetail(selectedSurah);
        }
    };

    public MainView(Context context) {
        super(context);

        setId(Res.Id.mainView);

        initView();
    }

    private void initView() {
        final SurahListView surahListView = new SurahListView(getContext(), new FetchAllSurahTask(this.quranRepository));
        surahListView.setOnViewEventListener(this.surahListEventListener);

        setToolbarTitle("Baca Qur'an");

        wrapView(surahListView);

        setOverlayAlpha(0.05f);
    }

    private void updateIsToolbarFlying(boolean isFlying) {
        if (this.isToolbarFlying != isFlying) {
            this.isToolbarFlying = isFlying;

            float newAlpha = isFlying ? 0.5f : 0.05f;
            animateOverlayAlpha(newAlpha);
        }
    }

    private void navigateToSurahDetail(Surah selectedSurah) {
        final SurahDetailView surahDetailView = new SurahDetailView(getContext(), selectedSurah);
        setToolbarTitle("Surah " + selectedSurah.getNameInLatin());
        surahDetailView.setId(Res.Id.surahDetailView);
        wrapView(surahDetailView);

        animateOverlayAlpha(0.05f);
    }
}
