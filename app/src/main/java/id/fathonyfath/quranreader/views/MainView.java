package id.fathonyfath.quranreader.views;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.utils.TypefaceLoader;
import id.fathonyfath.quranreader.utils.ViewBackStack;
import id.fathonyfath.quranreader.utils.ViewCallback;
import id.fathonyfath.quranreader.utils.ViewUtil;
import id.fathonyfath.quranreader.views.common.WrapperView;
import id.fathonyfath.quranreader.views.fontDownloader.FontDownloaderView;
import id.fathonyfath.quranreader.views.surahDetail.SurahDetailView;
import id.fathonyfath.quranreader.views.surahList.SurahListView;

public class MainView extends WrapperView {

    private final Map<Class, Integer> mappedClassToIndex;

    private ViewBackStack viewBackStack;
    private final ViewBackStack.Callback viewBackStackCallback = new ViewBackStack.Callback() {
        @Override
        public void onViewPushed(Class<? extends View> pushedView) {
            MainView.this.handleViewCallbackForPushedView(pushedView);

            MainView.this.updateViewBasedOnViewClass(pushedView);
        }

        @Override
        public void onViewPopped(Class<? extends View> poppedView) {
            Class<? extends View> topStackView = MainView.this.viewBackStack.peekView();

            if (topStackView != null) {
                MainView.this.updateViewBasedOnViewClass(topStackView);
            }

            MainView.this.handleViewCallbackForPoppedView(poppedView);
        }
    };

    private final FontDownloaderView.OnViewEventListener fontDownloaderEventListener = new FontDownloaderView.OnViewEventListener() {
        @Override
        public void onDownloadCompleted() {
            TypefaceLoader.invalidate();
            ViewUtil.reloadChildsTypeface(MainView.this);
            MainView.this.viewBackStack.popView();
            routeToSurahListView();
        }

        @Override
        public void onDownloadFailed() {
            TypefaceLoader.invalidate();
            MainView.this.viewBackStack.popView();
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

        this.mappedClassToIndex = new HashMap<>();

        this.viewBackStack = new ViewBackStack();
        this.viewBackStack.setCallback(this.viewBackStackCallback);

        initView();
    }

    public boolean onBackPressed() {
        return this.viewBackStack.popView();
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
        this.viewBackStack.setCallback(this.viewBackStackCallback);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (this.viewBackStack.isEmpty()) {
            routeToFontDownloaderView();
        } else {
            updateViewBasedOnViewClass(this.viewBackStack.peekView());
        }
    }

    private void initView() {
        setOverlayAlpha(0.1f);

        final FontDownloaderView fontDownloaderView = new FontDownloaderView(getContext());
        fontDownloaderView.setOnViewEventListener(this.fontDownloaderEventListener);

        final SurahListView surahListView = new SurahListView(getContext());
        surahListView.setOnViewEventListener(this.surahListEventListener);

        final SurahDetailView surahDetailView = new SurahDetailView(getContext());

        this.mappedClassToIndex.put(FontDownloaderView.class, addViewToContainer(fontDownloaderView));
        this.mappedClassToIndex.put(SurahListView.class, addViewToContainer(surahListView));
        this.mappedClassToIndex.put(SurahDetailView.class, addViewToContainer(surahDetailView));
    }

    private void routeToFontDownloaderView() {
        this.viewBackStack.pushView(FontDownloaderView.class);
    }

    private void routeToSurahListView() {
        this.viewBackStack.pushView(SurahListView.class);
    }

    private void routeToSurahDetailView(Surah selectedSurah) {
        SurahDetailView surahDetailView = findChildViewAtIndex(this.mappedClassToIndex.get(SurahDetailView.class));
        surahDetailView.setState(selectedSurah);

        this.viewBackStack.pushView(SurahDetailView.class);
    }

    private void updateViewBasedOnViewClass(Class<? extends View> viewClass) {
        showViewAtIndex(this.mappedClassToIndex.get(viewClass));
        showTitleForClass(viewClass);
    }

    private void showTitleForClass(Class<? extends View> classOfView) {
        if (classOfView == FontDownloaderView.class) {
            setToolbarTitle("Mengunduh font");
        } else if (classOfView == SurahListView.class) {
            setToolbarTitle("Baca Al-Qur'an");
        } else if (classOfView == SurahDetailView.class) {
            SurahDetailView surahDetailView = findChildViewAtIndex(this.mappedClassToIndex.get(SurahDetailView.class));
            setToolbarTitle("Al-Qur'an Surah " + surahDetailView.getSurahName());
        }
    }

    private void handleViewCallbackForPushedView(Class<? extends View> viewClass) {
        View view = findChildViewAtIndex(this.mappedClassToIndex.get(viewClass));
        if (view instanceof ViewCallback) {
            ViewCallback viewCallback = (ViewCallback) view;
            viewCallback.onStart();
        }
    }

    private void handleViewCallbackForPoppedView(Class<? extends View> viewClass) {
        View view = findChildViewAtIndex(this.mappedClassToIndex.get(viewClass));
        if (view instanceof ViewCallback) {
            ViewCallback viewCallback = (ViewCallback) view;
            viewCallback.onStop();
        }
    }

    private static class MainViewState extends BaseSavedState {

        private ViewBackStack viewBackStack;

        public MainViewState(Parcel source, ClassLoader loader) {
            super(source);

            this.viewBackStack = source.readParcelable(ViewBackStack.class.getClassLoader());
        }

        public MainViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeParcelable(this.viewBackStack, flags);
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
