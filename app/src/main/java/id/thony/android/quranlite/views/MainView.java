package id.thony.android.quranlite.views;

import android.content.Context;
import android.widget.Toast;

import id.thony.android.quranlite.Res;
import id.thony.android.quranlite.models.Surah;
import id.thony.android.quranlite.utils.TypefaceLoader;
import id.thony.android.quranlite.utils.ViewUtil;
import id.thony.android.quranlite.views.common.BackStackNavigationView;
import id.thony.android.quranlite.views.fontDownloader.FontDownloaderView;
import id.thony.android.quranlite.views.searchSurah.SearchSurahView;
import id.thony.android.quranlite.views.surahDetail.SurahDetailView;
import id.thony.android.quranlite.views.surahList.SurahListView;

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
        public void onSurahSelected(Surah selectedSurah, int lastReadingAyah) {
            routeToSurahDetailView(selectedSurah, lastReadingAyah);
        }

        @Override
        public void onSearchClicked() {
            routeToSearchSurahView();
        }
    };

    private final SearchSurahView.OnViewEventListener searchSurahEventListener = new SearchSurahView.OnViewEventListener() {
        @Override
        public void onSurahSelected(Surah selectedSurah, int lastReadingAyah) {
            routeToSurahDetailView(selectedSurah, lastReadingAyah);
        }

        @Override
        public void onCloseClicked() {
            MainView.this.popView();
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

        final SearchSurahView searchSurahView = new SearchSurahView(getContext());
        searchSurahView.setOnViewEventListener(this.searchSurahEventListener);

        final SurahDetailView surahDetailView = new SurahDetailView(getContext());

        this.registerView(FontDownloaderView.class, fontDownloaderView);
        this.registerView(SurahListView.class, surahListView);
        this.registerView(SearchSurahView.class, searchSurahView);
        this.registerView(SurahDetailView.class, surahDetailView);
    }

    private void routeToFontDownloaderView() {
        this.pushView(FontDownloaderView.class);
    }

    private void routeToSurahListView() {
        this.pushView(SurahListView.class);
    }

    private void routeToSearchSurahView() {
        this.pushView(SearchSurahView.class);
    }

    private void routeToSurahDetailView(Surah selectedSurah, int lastReadingAyah) {
        SurahDetailView surahDetailView = this.findChildWithClass(SurahDetailView.class);
        surahDetailView.setState(selectedSurah, lastReadingAyah);

        this.pushView(SurahDetailView.class);
    }
}
