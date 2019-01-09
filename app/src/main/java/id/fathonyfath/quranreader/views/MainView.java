package id.fathonyfath.quranreader.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import id.fathonyfath.quranreader.MainActivity;
import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.tasks.FetchAllSurahTask;
import id.fathonyfath.quranreader.views.common.WrapperView;
import id.fathonyfath.quranreader.views.surahList.SurahListView;

public class MainView extends WrapperView {

    private boolean isToolbarFlying = false;

    @SuppressLint("WrongConstant")
    private final QuranRepository quranRepository = (QuranRepository) getContext()
            .getSystemService(MainActivity.QURAN_REPOSITORY_SERVICE);

    private final SurahListView.OnScrollListener scrollListener = new SurahListView.OnScrollListener() {
        @Override
        public void onScroll(float scrollY) {
            if (scrollY > 0f) {
                updateIsToolbarFlying(true);
            } else {
                updateIsToolbarFlying(false);
            }
        }
    };

    public MainView(Context context) {
        super(context);

        setId(Res.Id.mainView);

        initView();
    }

    private void initView() {
        setToolbarTitle("Baca Qur'an");
        final SurahListView surahListView = new SurahListView(getContext(), new FetchAllSurahTask(quranRepository));
        surahListView.setOnScrollListener(scrollListener);
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
}
