package id.fathonyfath.quranreader.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import id.fathonyfath.quranreader.MainActivity;
import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.data.FontProvider;
import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.tasks.DownloadFontTask;
import id.fathonyfath.quranreader.tasks.FetchAllSurahTask;
import id.fathonyfath.quranreader.tasks.FetchSurahDetailTask;
import id.fathonyfath.quranreader.tasks.HasFontInstalledTask;
import id.fathonyfath.quranreader.utils.TypefaceLoader;
import id.fathonyfath.quranreader.views.common.WrapperView;
import id.fathonyfath.quranreader.views.fontDownloader.FontDownloaderView;
import id.fathonyfath.quranreader.views.surahDetail.SurahDetailView;
import id.fathonyfath.quranreader.views.surahList.SurahListView;

public class MainView extends WrapperView {

    private final Map<Class, Integer> mappedClassToIndex;

    private Stack<Class> viewBackStack;

    private boolean isToolbarFlying = false;

    @SuppressLint("WrongConstant")
    private final QuranRepository quranRepository = (QuranRepository) getContext()
            .getSystemService(MainActivity.QURAN_REPOSITORY_SERVICE);

    @SuppressLint("WrongConstant")
    private final FontProvider fontProvider = (FontProvider) getContext()
            .getSystemService(MainActivity.FONT_PROVIDER_SERVICE);

    private final FontDownloaderView.OnViewEventListener fontDownloaderEventListener = new FontDownloaderView.OnViewEventListener() {
        @Override
        public void onDownloadCompleted() {
            TypefaceLoader.invalidate();
            MainView.this.viewBackStack.pop();
            routeToSurahListView();
        }

        @Override
        public void onDownloadFailed() {
            TypefaceLoader.invalidate();
            MainView.this.viewBackStack.pop();
            routeToSurahListView();
            Toast.makeText(getContext(), "Gagal mengunduh font. Silahkan mencoba kembali dengan menutup aplikasi dan membukanya kembali.", Toast.LENGTH_LONG).show();
        }
    };

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
            routeToSurahDetailView(selectedSurah);
        }
    };

    public MainView(Context context) {
        super(context);

        setId(Res.Id.mainView);

        this.mappedClassToIndex = new HashMap<>();

        this.viewBackStack = new Stack<>();

        initView();
    }

    public boolean onBackPressed() {
        if (this.viewBackStack.size() > 1) {
            this.viewBackStack.pop();
            updateViewBasedOnBackStack();
            return true;
        }
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final MainViewState viewState = new MainViewState(super.onSaveInstanceState());
        viewState.viewBackStack = this.viewBackStack;
        return viewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final MainViewState viewState = (MainViewState) state;
        super.onRestoreInstanceState(viewState.getSuperState());
        this.viewBackStack = viewState.viewBackStack;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (this.viewBackStack.isEmpty()) {
            this.viewBackStack.push(FontDownloaderView.class);
        }

        updateViewBasedOnBackStack();
    }

    private void initView() {
        final FontDownloaderView fontDownloaderView = new FontDownloaderView(
                getContext(),
                new HasFontInstalledTask.Factory(this.fontProvider),
                new DownloadFontTask.Factory(this.fontProvider)
        );
        fontDownloaderView.setOnViewEventListener(this.fontDownloaderEventListener);

        final SurahListView surahListView = new SurahListView(
                getContext(),
                new FetchAllSurahTask.Factory(this.quranRepository)
        );
        surahListView.setOnViewEventListener(this.surahListEventListener);

        final SurahDetailView surahDetailView = new SurahDetailView(
                getContext(),
                new FetchSurahDetailTask.Factory(this.quranRepository)
        );

        this.mappedClassToIndex.put(FontDownloaderView.class, addViewToContainer(fontDownloaderView));
        this.mappedClassToIndex.put(SurahListView.class, addViewToContainer(surahListView));
        this.mappedClassToIndex.put(SurahDetailView.class, addViewToContainer(surahDetailView));

        setOverlayAlpha(0.05f);
    }

    private void updateIsToolbarFlying(boolean isFlying) {
        if (this.isToolbarFlying != isFlying) {
            this.isToolbarFlying = isFlying;

            float newAlpha = isFlying ? 0.5f : 0.05f;
            animateOverlayAlpha(newAlpha);
        }
    }

    private void routeToSurahListView() {
        this.viewBackStack.push(SurahListView.class);
        updateViewBasedOnBackStack();
    }

    private void routeToSurahDetailView(Surah selectedSurah) {
        SurahDetailView surahDetailView = findChildViewAtIndex(this.mappedClassToIndex.get(SurahDetailView.class));
        surahDetailView.setState(selectedSurah);

        this.viewBackStack.push(SurahDetailView.class);
        updateViewBasedOnBackStack();

        updateIsToolbarFlying(false);
    }

    private void updateViewBasedOnBackStack() {
        showViewAtIndex(this.mappedClassToIndex.get(this.viewBackStack.peek()));
    }

    private static class MainViewState extends BaseSavedState {

        private Stack<Class> viewBackStack = new Stack<>();

        public MainViewState(Parcel source, ClassLoader loader) {
            super(source);

            int size = source.readInt();
            Class[] classArray = (Class[]) source.readSerializable();

            for (int i = 0; i < size; i++) {
                this.viewBackStack.add(i, classArray[i]);
            }
        }

        public MainViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(this.viewBackStack.size());

            Class[] classArray = this.viewBackStack.toArray(new Class[0]);
            out.writeSerializable(classArray);
        }

        public static final Parcelable.Creator<MainViewState> CREATOR
                = new Parcelable.ClassLoaderCreator<MainViewState>() {
            @Override
            public MainViewState createFromParcel(Parcel in) {
                return new MainViewState(in, null);
            }

            @Override
            public MainViewState createFromParcel(Parcel in, ClassLoader loader) {
                return new MainViewState(in, loader);
            }

            @Override
            public MainViewState[] newArray(int size) {
                return new MainViewState[size];
            }
        };
    }
}
