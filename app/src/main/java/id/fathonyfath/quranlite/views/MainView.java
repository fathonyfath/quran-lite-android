package id.fathonyfath.quranlite.views;

import android.content.Context;
import android.widget.Toast;

import id.fathonyfath.quranlite.Res;
import id.fathonyfath.quranlite.models.Surah;
import id.fathonyfath.quranlite.utils.TypefaceLoader;
import id.fathonyfath.quranlite.utils.ViewUtil;
import id.fathonyfath.quranlite.views.common.BackStackNavigationView;
import id.fathonyfath.quranlite.views.fontDownloader.FontDownloaderView;
import id.fathonyfath.quranlite.views.surahDetail.SurahDetailView;
import id.fathonyfath.quranlite.views.surahList.SurahListView;

public class MainView extends BackStackNavigationView {

    private final FontDownloaderView.OnViewEventListener fontDownloaderEventListener = new FontDownloaderView.OnViewEventListener() {
        @Override
        public void onDownloadCompleted() {
            TypefaceLoader.invalidate();
            ViewUtil.reloadChildsTypeface(MainView.this);
            MainView.this.popView();
            routeToSurahListView();
        }

        @Override
        public void onDownloadFailed() {
            TypefaceLoader.invalidate();
            MainView.this.popView();
            routeToSurahListView();
            Toast.makeText(getContext(), "Gagal mengunduh font. Silahkan mencoba kembali dengan menutup aplikasi dan membukanya kembali.", Toast.LENGTH_LONG).show();
        }
    };

    private final SurahListView.OnViewEventListener surahListEventListener = new SurahListView.OnViewEventListener() {
        @Override
        public void onSurahSelected(Surah selectedSurah) {
            routeToSurahDetailView(selectedSurah);
        }
    };

    public MainView(Context context) {
        super(context);

        setId(Res.Id.mainView);

        initView();
    }

    @Override
    protected void initStack() {
        routeToFontDownloaderView();
    }

    private void initView() {
        final FontDownloaderView fontDownloaderView = new FontDownloaderView(getContext());
        fontDownloaderView.setOnViewEventListener(this.fontDownloaderEventListener);

        final SurahListView surahListView = new SurahListView(getContext());
        surahListView.setOnViewEventListener(this.surahListEventListener);

        final SurahDetailView surahDetailView = new SurahDetailView(getContext());

        this.registerView(FontDownloaderView.class, fontDownloaderView);
        this.registerView(SurahListView.class, surahListView);
        this.registerView(SurahDetailView.class, surahDetailView);
    }

    private void routeToFontDownloaderView() {
        this.pushView(FontDownloaderView.class);
    }

    private void routeToSurahListView() {
        this.pushView(SurahListView.class);
    }

    private void routeToSurahDetailView(Surah selectedSurah) {
        SurahDetailView surahDetailView = this.findChildWithClass(SurahDetailView.class);
        surahDetailView.setState(selectedSurah);

        this.pushView(SurahDetailView.class);
    }
}
