package id.thony.android.quranlite.views.surahList;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import id.thony.android.quranlite.Res;
import id.thony.android.quranlite.models.Bookmark;
import id.thony.android.quranlite.models.Surah;
import id.thony.android.quranlite.models.config.DayNightPreference;
import id.thony.android.quranlite.services.SurahDownloaderService;
import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.useCase.FetchAllSurahUseCase;
import id.thony.android.quranlite.useCase.GetBookmarkUseCase;
import id.thony.android.quranlite.useCase.GetDayNightPreferenceUseCase;
import id.thony.android.quranlite.useCase.PutDayNightPreferenceUseCase;
import id.thony.android.quranlite.useCase.UseCaseCallback;
import id.thony.android.quranlite.useCase.UseCaseProvider;
import id.thony.android.quranlite.utils.DialogUtil;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.ViewUtil;
import id.thony.android.quranlite.utils.dialogManager.DialogEvent;
import id.thony.android.quranlite.utils.dialogManager.DialogEventListener;
import id.thony.android.quranlite.utils.viewLifecycle.ViewCallback;
import id.thony.android.quranlite.views.common.BookmarkView;
import id.thony.android.quranlite.views.common.DayNightSwitchButton;
import id.thony.android.quranlite.views.common.DownloadView;
import id.thony.android.quranlite.views.common.GearView;
import id.thony.android.quranlite.views.common.ProgressView;
import id.thony.android.quranlite.views.common.RetryView;
import id.thony.android.quranlite.views.common.SearchView;
import id.thony.android.quranlite.views.common.WrapperView;
import id.thony.android.quranlite.views.noBookmarkDialog.NoBookmarkDialog;
import id.thony.android.quranlite.views.resumeBookmarkDialog.ResumeBookmarkDialog;

public class SurahListView extends WrapperView implements ViewCallback {

    private final List<Surah> surahList = new ArrayList<>();
    private final ListView surahListView;
    private final SurahAdapter surahAdapter;
    private final ProgressView progressView;
    private final RetryView retryView;
    private final BookmarkView bookmarkView;
    private final DayNightSwitchButton dayNightSwitchButton;
    private final DownloadView downloadView;
    private final GearView gearView;
    private final SearchView searchView;
    private final UseCaseCallback<DayNightPreference> dayNightPreferenceCallback = new UseCaseCallback<DayNightPreference>() {
        @Override
        public void onProgress(float progress) {

        }

        @Override
        public void onResult(DayNightPreference data) {
            // Do some cleanups
            unregisterAndClearGetDayNightPreferenceUseCaseCallback();

            setDayNightPreferenceData(data);
        }

        @Override
        public void onError(Throwable throwable) {
            unregisterAndClearGetDayNightPreferenceUseCaseCallback();
        }
    };
    private final UseCaseCallback<Bookmark> bookmarkUseCaseCallback = new UseCaseCallback<Bookmark>() {
        @Override
        public void onProgress(float progress) {

        }

        @Override
        public void onResult(Bookmark data) {
            // Do some cleanups
            unregisterAndClearGetBookmarkUseCaseCallback();

            setBookmarkData(data);
        }

        @Override
        public void onError(Throwable throwable) {
            unregisterAndClearGetBookmarkUseCaseCallback();
        }
    };
    private final UseCaseCallback<Boolean> putDayNightPreferenceCallback = new UseCaseCallback<Boolean>() {
        @Override
        public void onProgress(float progress) {

        }

        @Override
        public void onResult(Boolean isUpdateDifferentFromPrevious) {
            unregisterAndClearPutDayNightPreferenceUseCaseCallback();

            if (isUpdateDifferentFromPrevious) {
                ViewUtil.recreateActivity(SurahListView.this);
            } else {
                createAndRunGetDayNightPreferenceUseCase();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            unregisterAndClearPutDayNightPreferenceUseCaseCallback();
        }
    };
    private final View.OnClickListener onBookmarkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            processOnBookmarkClick();
        }
    };
    private final View.OnClickListener onDayNightPreferenceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createAndRunPutDayNightPreferenceUseCase(dayNightSwitchButton.cycleNextPreference());
        }
    };
    private final View.OnClickListener onDownloadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "Proses pengunduhan dimulai. Cek notifikasi untuk mengetahui perkembangan proses unduh.", Toast.LENGTH_SHORT).show();
            SurahDownloaderService.startService(getContext());
        }
    };
    private boolean isFailedToGetSurahList = false;
    private final UseCaseCallback<List<Surah>> fetchAllSurahCallback = new UseCaseCallback<List<Surah>>() {
        @Override
        public void onProgress(float progress) {
            updateTextProgress(progress);
        }

        @Override
        public void onResult(List<Surah> data) {
            // Don't forget to do some cleanups
            unregisterFetchAllSurahUseCaseCallback();
            clearFetchAllSurahUseCase();

            updateSurahList(data);
        }

        @Override
        public void onError(Throwable throwable) {
            // Don't forget to do some cleanups
            unregisterFetchAllSurahUseCaseCallback();
            clearFetchAllSurahUseCase();

            SurahListView.this.isFailedToGetSurahList = true;
            updateViewStateRetry();
        }
    };
    private final View.OnClickListener onRetryClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            SurahListView.this.isFailedToGetSurahList = false;

            updateViewStateLoading();

            if (!tryToRestoreFetchAllSurahUseCase()) {
                createAndRunFetchAllSurahUseCase();
            }
        }
    };
    private OnViewEventListener onViewEventListener;
    private final AdapterView.OnItemClickListener onSurahItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (SurahListView.this.onViewEventListener != null) {
                Surah selectedSurah = SurahListView.this.surahAdapter.getItem(position);
                SurahListView.this.onViewEventListener.onSurahSelected(selectedSurah, 0);
            }
        }
    };
    private final DialogEventListener dialogEventListener = new DialogEventListener() {
        @Override
        public void onEvent(DialogEvent event, Parcelable arguments) {
            if (event instanceof ResumeBookmarkDialog.ResumeBookmarkEvent) {
                if (arguments.getClass().isAssignableFrom(Bookmark.class)) {
                    final Bookmark bookmark = (Bookmark) arguments;
                    resumeFromBookmark(bookmark);
                }
            }
        }
    };
    private final OnClickListener onGearClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
    private final OnClickListener onSearchClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onViewEventListener != null) {
                onViewEventListener.onSearchClicked();
            }
        }
    };

    public SurahListView(Context context) {
        super(context);

        setId(Res.Id.surahListView);

        this.surahListView = new ListView(getContext());
        this.surahAdapter = new SurahAdapter(getContext(), this.surahList);

        this.progressView = new ProgressView(getContext());
        this.retryView = new RetryView(getContext());

        this.bookmarkView = new BookmarkView(getContext());
        this.bookmarkView.setOnClickListener(this.onBookmarkClickListener);

        this.dayNightSwitchButton = new DayNightSwitchButton(getContext());
        this.dayNightSwitchButton.setOnClickListener(this.onDayNightPreferenceClickListener);

        this.downloadView = new DownloadView(getContext());
        this.downloadView.setOnClickListener(this.onDownloadClickListener);

        this.gearView = new GearView(getContext());
        this.gearView.setOnClickListener(this.onGearClickListener);

        this.searchView = new SearchView(getContext());
        this.searchView.setOnClickListener(this.onSearchClickListener);

        this.setToolbarTitle("Al-Qur'an Lite");
        this.setElevationAlpha(0.1f);
        this.setToolbarLeftView(this.searchView);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SurahListViewState viewState = new SurahListViewState(super.onSaveInstanceState());
        viewState.surahList = this.surahList;
        viewState.isFailedToGetSurahList = this.isFailedToGetSurahList;
        return viewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SurahListViewState viewState = (SurahListViewState) state;
        super.onRestoreInstanceState(viewState.getSuperState());
        restoreSurahList(viewState.surahList);
        this.isFailedToGetSurahList = viewState.isFailedToGetSurahList;
    }

    @Override
    public void onStart() {
        updateViewStateComplete();
        updateTextProgress(0f);
    }

    @Override
    public void onResume() {
        updateViewStateComplete();

        this.surahListView.setOnItemClickListener(onSurahItemClickListener);

        if (this.surahList.isEmpty() && !this.isFailedToGetSurahList) {
            updateViewStateLoading();

            if (!tryToRestoreFetchAllSurahUseCase()) {
                createAndRunFetchAllSurahUseCase();
            }
        }

        if (this.isFailedToGetSurahList) {
            updateViewStateRetry();
        }

        createAndRunGetDayNightPreferenceUseCase();
        createAndRunGetBookmarkUseCase();

        DialogUtil.addListener(this, this.dialogEventListener);
    }

    @Override
    public void onPause() {
        this.surahListView.setOnItemClickListener(null);

        unregisterFetchAllSurahUseCaseCallback();
        unregisterAndClearGetDayNightPreferenceUseCaseCallback();
        unregisterAndClearPutDayNightPreferenceUseCaseCallback();

        DialogUtil.removeListener(this, this.dialogEventListener);
    }

    @Override
    public void onStop() {
        clearFetchAllSurahUseCase();
    }

    public void setOnViewEventListener(OnViewEventListener onViewEventListener) {
        this.onViewEventListener = onViewEventListener;
    }

    private void initConfiguration() {
        setBackgroundColor(Color.WHITE);
    }

    private void initView() {
        this.surahListView.setId(Res.Id.surahListView_surahListView);
        this.surahListView.setAdapter(this.surahAdapter);

        addView(this.surahListView);

        final FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressParams.gravity = Gravity.CENTER;
        this.progressView.setLayoutParams(progressParams);
        this.progressView.setId(Res.Id.surahListView_progressView);

        addView(this.progressView);

        final FrameLayout.LayoutParams retryParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        retryParams.gravity = Gravity.CENTER;
        this.retryView.setLayoutParams(progressParams);
        this.retryView.setId(Res.Id.surahListView_retryView);
        this.retryView.setOnRetryClickListener(this.onRetryClickListener);

        addView(this.retryView);
    }

    private void processOnBookmarkClick() {
        final Bookmark bookmark = this.bookmarkView.getBookmark();
        if (bookmark == null) {
            DialogUtil.showDialog(this, NoBookmarkDialog.class, null);
        } else {
            DialogUtil.showDialog(this, ResumeBookmarkDialog.class, bookmark);
        }
    }

    private boolean tryToRestoreFetchAllSurahUseCase() {
        FetchAllSurahUseCase useCase = UseCaseProvider.getUseCase(FetchAllSurahUseCase.class);
        if (useCase != null) {
            useCase.setCallback(this.fetchAllSurahCallback);
            return true;
        }
        return false;
    }

    private void createAndRunFetchAllSurahUseCase() {
        FetchAllSurahUseCase useCase = UseCaseProvider.createUseCase(FetchAllSurahUseCase.class);
        useCase.setCallback(this.fetchAllSurahCallback);
        useCase.run();
    }

    private void createAndRunGetDayNightPreferenceUseCase() {
        GetDayNightPreferenceUseCase useCase = UseCaseProvider.createUseCase(GetDayNightPreferenceUseCase.class);
        useCase.setCallback(this.dayNightPreferenceCallback);
        useCase.run();
    }

    private void createAndRunGetBookmarkUseCase() {
        GetBookmarkUseCase useCase = UseCaseProvider.createUseCase(GetBookmarkUseCase.class);
        useCase.setCallback(this.bookmarkUseCaseCallback);
        useCase.run();
    }

    private void createAndRunPutDayNightPreferenceUseCase(DayNightPreference updateWith) {
        PutDayNightPreferenceUseCase useCase = UseCaseProvider.createUseCase(PutDayNightPreferenceUseCase.class);
        useCase.setArguments(updateWith);
        useCase.setCallback(this.putDayNightPreferenceCallback);
        useCase.run();
    }

    private void unregisterFetchAllSurahUseCaseCallback() {
        FetchAllSurahUseCase useCase = UseCaseProvider.getUseCase(FetchAllSurahUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }
    }

    private void unregisterAndClearGetDayNightPreferenceUseCaseCallback() {
        GetDayNightPreferenceUseCase useCase = UseCaseProvider.getUseCase(GetDayNightPreferenceUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }

        UseCaseProvider.clearUseCase(GetDayNightPreferenceUseCase.class);
    }

    private void unregisterAndClearGetBookmarkUseCaseCallback() {
        GetBookmarkUseCase useCase = UseCaseProvider.getUseCase(GetBookmarkUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }

        UseCaseProvider.clearUseCase(GetBookmarkUseCase.class);
    }

    private void unregisterAndClearPutDayNightPreferenceUseCaseCallback() {
        PutDayNightPreferenceUseCase useCase = UseCaseProvider.getUseCase(PutDayNightPreferenceUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }

        UseCaseProvider.clearUseCase(PutDayNightPreferenceUseCase.class);
    }

    private void clearFetchAllSurahUseCase() {
        UseCaseProvider.clearUseCase(FetchAllSurahUseCase.class);
    }

    private void setDayNightPreferenceData(DayNightPreference preference) {
        this.dayNightSwitchButton.setDayNightPreference(preference);
        refreshRightToolbar();
    }

    private void setBookmarkData(Bookmark bookmark) {
        this.bookmarkView.setBookmark(bookmark);
        refreshRightToolbar();
    }

    private void refreshRightToolbar() {
        final LinkedHashSet<View> views = new LinkedHashSet<>();
        views.add(this.bookmarkView);
        views.add(this.gearView);
        setToolbarRightViews(views);
    }

    private void updateSurahList(List<Surah> surahList) {
        updateViewStateComplete();
        updateTextProgress(0f);

        this.surahAdapter.clear();
        this.surahAdapter.addAll(surahList);
        this.surahAdapter.notifyDataSetChanged();
    }

    private void updateTextProgress(float progress) {
        this.progressView.updateProgress(progress);
    }

    private void updateViewStateLoading() {
        this.progressView.setVisibility(View.VISIBLE);
        this.surahListView.setVisibility(View.GONE);
        this.retryView.setVisibility(View.GONE);
    }

    private void updateViewStateRetry() {
        this.progressView.setVisibility(View.GONE);
        this.surahListView.setVisibility(View.GONE);
        this.retryView.setVisibility(View.VISIBLE);
    }

    private void updateViewStateComplete() {
        this.progressView.setVisibility(View.GONE);
        this.surahListView.setVisibility(View.VISIBLE);
        this.retryView.setVisibility(View.GONE);
    }

    private void restoreSurahList(List<Surah> surahList) {
        this.surahList.clear();
        this.surahList.addAll(surahList);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.baseColor());
            ViewUtil.setDefaultSelectableBackgroundDrawable(this.surahListView, theme.contrastColor());
        }
    }

    private void resumeFromBookmark(Bookmark bookmark) {
        final int position = bookmark.getSurahNumber() - 1;
        surahListView.setSelection(position);
        if (this.onViewEventListener != null) {
            Surah selectedSurah = this.surahAdapter.getItem(position);
            this.onViewEventListener.onSurahSelected(selectedSurah, bookmark.getLastReadAyah());
        }
    }

    public interface OnViewEventListener {
        void onSurahSelected(Surah selectedSurah, int lastReadingAyah);

        void onSearchClicked();
    }

    private static class SurahListViewState extends BaseSavedState {

        public static final Parcelable.Creator<SurahListViewState> CREATOR
                = new Parcelable.ClassLoaderCreator<SurahListViewState>() {
            @Override
            public SurahListViewState createFromParcel(Parcel in) {
                return new SurahListViewState(in, null);
            }

            @Override
            public SurahListViewState createFromParcel(Parcel in, ClassLoader loader) {
                return new SurahListViewState(in, loader);
            }

            @Override
            public SurahListViewState[] newArray(int size) {
                return new SurahListViewState[size];
            }
        };
        private List<Surah> surahList = new ArrayList<>();
        private boolean isFailedToGetSurahList = false;

        public SurahListViewState(Parcel source, ClassLoader loader) {
            super(source);

            int size = source.readInt();
            Surah[] surahArray = new Surah[size];
            source.readTypedArray(surahArray, Surah.CREATOR);

            this.surahList.clear();
            this.surahList.addAll(Arrays.asList(surahArray));

            int isFailedToGetSurahList = source.readInt();
            this.isFailedToGetSurahList = isFailedToGetSurahList > 1;
        }

        public SurahListViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(this.surahList.size());
            Surah[] surahArray = this.surahList.toArray(new Surah[0]);
            out.writeTypedArray(surahArray, flags);

            out.writeInt((this.isFailedToGetSurahList) ? 1 : 0);
        }
    }
}
