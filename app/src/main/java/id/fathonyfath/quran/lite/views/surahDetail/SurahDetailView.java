package id.fathonyfath.quran.lite.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import id.fathonyfath.quran.lite.Res;
import id.fathonyfath.quran.lite.models.Surah;
import id.fathonyfath.quran.lite.models.SurahDetail;
import id.fathonyfath.quran.lite.models.config.DayNightPreference;
import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.useCase.FetchSurahDetailUseCase;
import id.fathonyfath.quran.lite.useCase.GetDayNightPreferenceUseCase;
import id.fathonyfath.quran.lite.useCase.PutDayNightPreferenceUseCase;
import id.fathonyfath.quran.lite.useCase.UseCaseCallback;
import id.fathonyfath.quran.lite.useCase.UseCaseProvider;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.ViewUtil;
import id.fathonyfath.quran.lite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quran.lite.views.common.DayNightSwitchButton;
import id.fathonyfath.quran.lite.views.common.ProgressView;
import id.fathonyfath.quran.lite.views.common.WrapperView;
import id.fathonyfath.quran.lite.views.surahList.SurahListView;
import id.fathonyfath.quran.lite.views.surahList.SurahView;

public class SurahDetailView extends WrapperView implements ViewCallback {

    private final List<AyahDetailViewType> ayahViewTypeList = new ArrayList<>();
    private final ListView ayahListView;
    private final AyahDetailAdapter ayahDetailAdapter;
    private final ProgressView progressView;
    private final DayNightSwitchButton dayNightSwitchButton;
    private Surah currentSurah;
    private Parcelable listViewState;
    private boolean newPage = false;
    private int firstVisibleItem = 0;
    private int lastVisibleItem = -1;
    private final UseCaseCallback<SurahDetail> fetchSurahDetailCallback = new UseCaseCallback<SurahDetail>() {
        @Override
        public void onProgress(float progress) {
            updateTextProgress(progress);
        }

        @Override
        public void onResult(SurahDetail data) {
            // Don't forget to do some cleanups
            unregisterFetchSurahDetailUseCaseCallback();
            clearSurahDetailUseCase();

            processSurahDetail(data);
        }

        @Override
        public void onError(Throwable throwable) {
            // Don't forget to do some cleanups
            unregisterFetchSurahDetailUseCaseCallback();
            clearSurahDetailUseCase();
        }
    };
    private final UseCaseCallback<DayNightPreference> dayNightPreferenceCallback = new UseCaseCallback<DayNightPreference>() {
        @Override
        public void onProgress(float progress) {

        }

        @Override
        public void onResult(DayNightPreference data) {
            // Do some cleanups
            unregisterAndClearGetDayNightPreferenceUseCaseCallback();

            setDayNightPreferenceView(data);
        }

        @Override
        public void onError(Throwable throwable) {
            unregisterAndClearGetDayNightPreferenceUseCaseCallback();
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
                ViewUtil.recreateActivity(SurahDetailView.this);
            } else {
                createAndRunGetDayNightPreferenceUseCase();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            unregisterAndClearPutDayNightPreferenceUseCaseCallback();
        }
    };
    private final AbsListView.OnScrollListener onSurahScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
            updateTitleWithSurahNumber(firstVisibleItem, lastVisibleItem);
        }
    };
    private final View.OnClickListener onDayNightPreferenceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createAndRunPutDayNightPreferenceUseCase(dayNightSwitchButton.cycleNextPreference());
        }
    };

    public SurahDetailView(Context context) {
        super(context);

        setId(Res.Id.surahDetailView);

        this.ayahListView = new ListView(context);
        this.ayahListView.setId(Res.Id.surahDetailView_ayahListView);
        this.ayahDetailAdapter = new AyahDetailAdapter(context, this.ayahViewTypeList);

        this.progressView = new ProgressView(getContext());

        this.dayNightSwitchButton = new DayNightSwitchButton(getContext());
        this.dayNightSwitchButton.setOnClickListener(onDayNightPreferenceClickListener);

        this.setElevationAlpha(0.1f);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SurahDetailViewState surahDetailViewState = new SurahDetailViewState(super.onSaveInstanceState());
        surahDetailViewState.currentSurah = this.currentSurah;
        surahDetailViewState.listViewState = this.ayahListView.onSaveInstanceState();
        return surahDetailViewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SurahDetailViewState surahDetailViewState = (SurahDetailViewState) state;
        super.onRestoreInstanceState(surahDetailViewState.getSuperState());
        this.currentSurah = surahDetailViewState.currentSurah;
        this.listViewState = surahDetailViewState.listViewState;
    }

    @Override
    public void onStart() {
        this.progressView.setVisibility(View.GONE);
        updateTextProgress(0f);

    }

    @Override
    public void onResume() {
        this.ayahListView.setOnScrollListener(this.onSurahScrollListener);

        if (this.currentSurah != null) {
            this.progressView.setVisibility(View.VISIBLE);

            updateToolbarTitle(this.currentSurah.getNameInLatin(), this.currentSurah.getNumber(),
                    -1, -1);

            if (!tryToRestoreUseCase()) {
                createAndRunFetchSurahDetailUseCase();
            }
        } else {
            ViewUtil.onBackPressed(this);
        }

        createAndRunGetDayNightPreferenceUseCase();
    }

    @Override
    public void onPause() {
        clearView();

        this.ayahListView.setOnScrollListener(null);

        unregisterFetchSurahDetailUseCaseCallback();
        unregisterAndClearGetDayNightPreferenceUseCaseCallback();
        unregisterAndClearPutDayNightPreferenceUseCaseCallback();
    }

    @Override
    public void onStop() {
        clearSurahDetailUseCase();

        unregisterAndClearGetDayNightPreferenceUseCaseCallback();
        unregisterAndClearPutDayNightPreferenceUseCaseCallback();
    }

    public void setState(Surah selectedSurah) {
        this.currentSurah = selectedSurah;
        this.newPage = true;
    }

    private void initConfiguration() {
        setBackgroundColor(Color.WHITE);
    }

    private void initView() {
        this.ayahListView.setAdapter(this.ayahDetailAdapter);
        this.ayahListView.setDivider(null);

        addView(this.ayahListView);

        final FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressParams.gravity = Gravity.CENTER;
        this.progressView.setLayoutParams(progressParams);

        addView(this.progressView);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.baseColor());
        }
    }

    private void setDayNightPreferenceView(DayNightPreference preference) {
        final HashSet<View> views = new HashSet<>();
        this.dayNightSwitchButton.setDayNightPreference(preference);
        views.add(this.dayNightSwitchButton);
        setToolbarRightViews(views);
    }

    private void processSurahDetail(SurahDetail surahDetail) {
        this.progressView.setVisibility(View.GONE);
        updateTextProgress(0f);

        this.ayahViewTypeList.clear();


        if (surahDetail.getNumber() != 1) {
            this.ayahViewTypeList.add(new AyahDetailViewType.BasmalahViewModel());
        }

        for (Map.Entry<Integer, String> entry : surahDetail.getContents().entrySet()) {
            String translation = surahDetail.getSurahTranslation().getContents().get(entry.getKey());
            if (translation == null) {
                translation = "";
            }
            this.ayahViewTypeList.add(new AyahDetailViewType.AyahViewModel(entry.getKey(), entry.getValue(), translation));
        }

        this.ayahDetailAdapter.notifyDataSetChanged();

        if (this.listViewState != null && !newPage) {
            this.ayahListView.onRestoreInstanceState(this.listViewState);
        }
    }

    private void updateTextProgress(float progress) {
        this.progressView.updateProgress(progress);
    }

    private void updateTitleWithSurahNumber(int firstVisibleItem, int lastVisibleItem) {
        if (this.firstVisibleItem == firstVisibleItem && this.lastVisibleItem == lastVisibleItem) {
            return;
        }

        this.firstVisibleItem = firstVisibleItem;
        this.lastVisibleItem = lastVisibleItem;

        View firstView = this.ayahListView.getChildAt(0);
        View lastView = this.ayahListView.getChildAt(this.ayahListView.getChildCount() - 1);

        if (firstView instanceof AyahView && lastView instanceof AyahView) {
            AyahView firstAyahView = (AyahView) firstView;
            AyahView lastAyahView = (AyahView) lastView;
            final int firstAyahNumber = firstAyahView.getAyahViewModel().ayahNumber;
            final int lastAyahNumber = lastAyahView.getAyahViewModel().ayahNumber;

            updateToolbarTitle(this.currentSurah.getNameInLatin(), this.currentSurah.getNumber(),
                    firstAyahNumber, lastAyahNumber);
        } else if (firstView instanceof BasmalahView && lastView instanceof AyahView) {
            // Since the possibility BasmallahView only searchable only on topmost we only lookup
            // for firstView and check the view after
            AyahView firstAyahView = (AyahView) this.ayahListView.getChildAt(1);
            AyahView lastAyahView = (AyahView) lastView;

            final int firstAyahNumber = firstAyahView.getAyahViewModel().ayahNumber;
            final int lastAyahNumber = lastAyahView.getAyahViewModel().ayahNumber;

            updateToolbarTitle(this.currentSurah.getNameInLatin(), this.currentSurah.getNumber(),
                    firstAyahNumber, lastAyahNumber);
        }
    }

    private void updateToolbarTitle(String surahNameInLatin, int surahNumber, int firstAyahNumber, int lastAyahNumber) {
        if (firstAyahNumber < 0 && lastAyahNumber < 0) {
            this.setToolbarTitle("QS. " + this.currentSurah.getNameInLatin()
                    + " [" + this.currentSurah.getNumber() + "]");
            return;
        }

        if (firstAyahNumber == lastAyahNumber) {
            this.setToolbarTitle("QS. " + this.currentSurah.getNameInLatin()
                    + " [" + this.currentSurah.getNumber() + "]"
                    + ": " + firstAyahNumber);
        } else {
            this.setToolbarTitle("QS. " + this.currentSurah.getNameInLatin()
                    + " [" + this.currentSurah.getNumber() + "]"
                    + ": " + firstAyahNumber + " - " + lastAyahNumber);
        }
    }

    private boolean tryToRestoreUseCase() {
        FetchSurahDetailUseCase useCase = UseCaseProvider.getUseCase(FetchSurahDetailUseCase.class);
        if (useCase != null) {
            useCase.setCallback(this.fetchSurahDetailCallback);
            return true;
        }
        return false;
    }

    private void createAndRunFetchSurahDetailUseCase() {
        FetchSurahDetailUseCase useCase = UseCaseProvider.createUseCase(FetchSurahDetailUseCase.class);
        useCase.setCallback(this.fetchSurahDetailCallback);
        useCase.setArguments(this.currentSurah);
        useCase.run();
    }

    private void createAndRunPutDayNightPreferenceUseCase(DayNightPreference updateWith) {
        PutDayNightPreferenceUseCase useCase = UseCaseProvider.createUseCase(PutDayNightPreferenceUseCase.class);
        useCase.setUpdateWith(updateWith);
        useCase.setCallback(this.putDayNightPreferenceCallback);
        useCase.run();
    }

    private void createAndRunGetDayNightPreferenceUseCase() {
        GetDayNightPreferenceUseCase useCase = UseCaseProvider.createUseCase(GetDayNightPreferenceUseCase.class);
        useCase.setCallback(this.dayNightPreferenceCallback);
        useCase.run();
    }

    private void unregisterFetchSurahDetailUseCaseCallback() {
        FetchSurahDetailUseCase useCase = UseCaseProvider.getUseCase(FetchSurahDetailUseCase.class);
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

    private void unregisterAndClearPutDayNightPreferenceUseCaseCallback() {
        PutDayNightPreferenceUseCase useCase = UseCaseProvider.getUseCase(PutDayNightPreferenceUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }

        UseCaseProvider.clearUseCase(PutDayNightPreferenceUseCase.class);
    }

    private void clearSurahDetailUseCase() {
        UseCaseProvider.clearUseCase(FetchSurahDetailUseCase.class);
    }

    private void clearView() {
        this.ayahViewTypeList.clear();
        this.ayahDetailAdapter.clear();
    }

    private static class SurahDetailViewState extends BaseSavedState {

        public static final Parcelable.Creator<SurahDetailViewState> CREATOR
                = new Parcelable.ClassLoaderCreator<SurahDetailViewState>() {
            @Override
            public SurahDetailViewState createFromParcel(Parcel in) {
                return new SurahDetailViewState(in, null);
            }

            @Override
            public SurahDetailViewState createFromParcel(Parcel in, ClassLoader loader) {
                return new SurahDetailViewState(in, loader);
            }

            @Override
            public SurahDetailViewState[] newArray(int size) {
                return new SurahDetailViewState[size];
            }
        };
        private Surah currentSurah;
        private Parcelable listViewState;

        public SurahDetailViewState(Parcel source, ClassLoader loader) {
            super(source);

            this.currentSurah = source.readParcelable(Surah.class.getClassLoader());
            this.listViewState = source.readParcelable(loader);
        }

        public SurahDetailViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeParcelable(this.currentSurah, flags);
            out.writeParcelable(this.listViewState, flags);
        }
    }
}
