package dev.fathony.android.quranlite.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import dev.fathony.android.quranlite.Res;
import dev.fathony.android.quranlite.models.Bookmark;
import dev.fathony.android.quranlite.models.SelectedAyah;
import dev.fathony.android.quranlite.models.SelectedTafsir;
import dev.fathony.android.quranlite.models.Surah;
import dev.fathony.android.quranlite.models.SurahDetail;
import dev.fathony.android.quranlite.models.config.DayNightPreference;
import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.useCase.FetchSurahDetailUseCase;
import dev.fathony.android.quranlite.useCase.GetDayNightPreferenceUseCase;
import dev.fathony.android.quranlite.useCase.PutBookmarkUseCase;
import dev.fathony.android.quranlite.useCase.PutDayNightPreferenceUseCase;
import dev.fathony.android.quranlite.useCase.UseCaseCallback;
import dev.fathony.android.quranlite.useCase.UseCaseProvider;
import dev.fathony.android.quranlite.utils.DialogUtil;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;
import dev.fathony.android.quranlite.utils.ViewUtil;
import dev.fathony.android.quranlite.utils.dialogManager.DialogEvent;
import dev.fathony.android.quranlite.utils.dialogManager.DialogEventListener;
import dev.fathony.android.quranlite.utils.viewLifecycle.ViewCallback;
import dev.fathony.android.quranlite.views.ayahDetailDialog.AyahDetailDialog;
import dev.fathony.android.quranlite.views.common.DayNightSwitchButton;
import dev.fathony.android.quranlite.views.common.ProgressView;
import dev.fathony.android.quranlite.views.common.RetryView;
import dev.fathony.android.quranlite.views.common.WrapperView;
import dev.fathony.android.quranlite.views.readTafsirDialog.ReadTafsirDialog;

public class SurahDetailView extends WrapperView implements ViewCallback {

    private final List<AyahDetailViewType> ayahViewTypeList = new ArrayList<>();
    private final ListView ayahListView;
    private final AyahDetailAdapter ayahDetailAdapter;
    private final ProgressView progressView;
    private final RetryView retryView;
    private final DayNightSwitchButton dayNightSwitchButton;
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
    private final View.OnClickListener onDayNightPreferenceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createAndRunPutDayNightPreferenceUseCase(dayNightSwitchButton.cycleNextPreference());
        }
    };
    private final float revealThreshold;
    private final UseCaseCallback<Boolean> putBookmarkCallback = new UseCaseCallback<Boolean>() {
        @Override
        public void onProgress(float progress) {

        }

        @Override
        public void onResult(Boolean success) {
            Toast.makeText(getContext(), "Berhasil menandai ayat.", Toast.LENGTH_SHORT).show();

            unregisterAndClearPutBookmarkUseCaseCallback();
        }

        @Override
        public void onError(Throwable throwable) {
            unregisterAndClearPutBookmarkUseCaseCallback();
        }
    };
    private boolean isFailedToGetSurahDetail = false;
    private Surah currentSurah;
    private final AbsListView.OnItemLongClickListener onSurahLongClickListener = new AbsListView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            AyahDetailViewType ayahViewType = SurahDetailView.this.ayahDetailAdapter.getItem(position);
            if (ayahViewType instanceof AyahDetailViewType.AyahViewModel) {
                AyahDetailViewType.AyahViewModel ayahViewModel = (AyahDetailViewType.AyahViewModel) ayahViewType;
                openAyahDetailDialog(ayahViewModel);

                return true;
            }
            return false;
        }
    };
    private SurahDetail currentSurahDetail;
    private final DialogEventListener dialogEventListener = new DialogEventListener() {
        @Override
        public void onEvent(DialogEvent event, Parcelable arguments) {
            if (event instanceof AyahDetailDialog.ReadTafsirEvent) {
                if (arguments.getClass().isAssignableFrom(SelectedAyah.class)) {
                    final SelectedAyah selectedAyah = (SelectedAyah) arguments;
                    readTafsir(selectedAyah);
                }
            } else if (event instanceof AyahDetailDialog.PutBookmarkEvent) {
                if (arguments.getClass().isAssignableFrom(SelectedAyah.class)) {
                    final SelectedAyah selectedAyah = (SelectedAyah) arguments;
                    putBookmark(selectedAyah);
                }
            }
        }
    };
    private int lastReadingAyah;
    private Parcelable listViewState;
    private boolean newPage = false;
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

            updateViewStateComplete();

            processSurahDetail(data);
        }

        @Override
        public void onError(Throwable throwable) {
            // Don't forget to do some cleanups
            unregisterFetchSurahDetailUseCaseCallback();
            clearSurahDetailUseCase();

            SurahDetailView.this.isFailedToGetSurahDetail = true;
            updateViewStateRetry();
        }
    };
    private final View.OnClickListener onRetryClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            SurahDetailView.this.isFailedToGetSurahDetail = false;

            updateViewStateLoading();

            if (!tryToRestoreFetchSurahDetailUseCase()) {
                createAndRunFetchSurahDetailUseCase();
            }
        }
    };
    private int firstAyahNumber = 0;
    private int lastAyahNumber = -1;
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

    public SurahDetailView(Context context) {
        super(context);

        setId(Res.Id.surahDetailView);

        this.ayahListView = new ListView(context);
        this.ayahListView.setId(Res.Id.surahDetailView_ayahListView);
        this.ayahDetailAdapter = new AyahDetailAdapter(context, this.ayahViewTypeList);

        this.progressView = new ProgressView(getContext());
        this.retryView = new RetryView(getContext());

        this.dayNightSwitchButton = new DayNightSwitchButton(getContext());
        this.dayNightSwitchButton.setOnClickListener(onDayNightPreferenceClickListener);

        this.revealThreshold = UnitConverter.fromDpToPx(context, 48f);

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
        surahDetailViewState.isFailedToGetSurahDetail = this.isFailedToGetSurahDetail;
        return surahDetailViewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SurahDetailViewState surahDetailViewState = (SurahDetailViewState) state;
        super.onRestoreInstanceState(surahDetailViewState.getSuperState());
        this.currentSurah = surahDetailViewState.currentSurah;
        this.listViewState = surahDetailViewState.listViewState;
        this.isFailedToGetSurahDetail = surahDetailViewState.isFailedToGetSurahDetail;
    }

    @Override
    public void onStart() {
        updateViewStateComplete();
        updateTextProgress(0f);

        this.isFailedToGetSurahDetail = false;
    }

    @Override
    public void onResume() {
        updateViewStateComplete();
        this.ayahListView.setOnScrollListener(this.onSurahScrollListener);
        this.ayahListView.setOnItemLongClickListener(this.onSurahLongClickListener);

        if (this.currentSurah != null && !isFailedToGetSurahDetail) {
            updateViewStateLoading();

            updateToolbarTitle(this.currentSurah.getNameInLatin(), this.currentSurah.getNumber(),
                    -1, -1);

            if (!tryToRestoreFetchSurahDetailUseCase()) {
                createAndRunFetchSurahDetailUseCase();
            }
        } else if (this.currentSurah != null) {
            // If we found Failed to retry true and this currentSurah is not null
            updateToolbarTitle(this.currentSurah.getNameInLatin(), this.currentSurah.getNumber(),
                    -1, -1);

            updateViewStateRetry();
        } else {
            ViewUtil.onBackPressed(this);
        }

        createAndRunGetDayNightPreferenceUseCase();

        DialogUtil.addListener(this, this.dialogEventListener);
    }

    @Override
    public void onPause() {
        clearView();

        this.ayahListView.setOnScrollListener(null);
        this.ayahListView.setOnItemLongClickListener(null);

        unregisterFetchSurahDetailUseCaseCallback();
        unregisterAndClearGetDayNightPreferenceUseCaseCallback();
        unregisterAndClearPutDayNightPreferenceUseCaseCallback();

        DialogUtil.removeListener(this, this.dialogEventListener);
    }

    @Override
    public void onStop() {
        clearSurahDetailUseCase();
    }

    public void setState(Surah selectedSurah, int lastReadingAyah) {
        this.currentSurah = selectedSurah;
        this.lastReadingAyah = lastReadingAyah;
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
        this.progressView.setId(Res.Id.surahDetailView_progressView);

        addView(this.progressView);

        final FrameLayout.LayoutParams retryParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        retryParams.gravity = Gravity.CENTER;
        this.retryView.setLayoutParams(retryParams);
        this.retryView.setId(Res.Id.surahDetailView_retryView);
        this.retryView.setOnRetryClickListener(this.onRetryClickListener);

        addView(this.retryView);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.setBackgroundColor(theme.baseColor());
            ViewUtil.setDefaultSelectableBackgroundDrawable(this.ayahListView, theme.contrastColor());
        }
    }

    private void setDayNightPreferenceView(DayNightPreference preference) {
        final HashSet<View> views = new HashSet<>();
        this.dayNightSwitchButton.setDayNightPreference(preference);
        views.add(this.dayNightSwitchButton);
        setToolbarRightViews(views);
    }

    private void processSurahDetail(SurahDetail surahDetail) {
        this.currentSurahDetail = surahDetail;

        this.progressView.setVisibility(View.GONE);
        updateTextProgress(0f);

        this.ayahViewTypeList.clear();

        boolean basmalahAddedProgrammatically = false;

        if (surahDetail.getNumber() != 1) {
            this.ayahViewTypeList.add(new AyahDetailViewType.BasmalahViewModel());
            basmalahAddedProgrammatically = true;
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

        if (this.lastReadingAyah > 0 && newPage) {
            if (basmalahAddedProgrammatically) {
                this.ayahListView.setSelection(this.lastReadingAyah);
            } else {
                this.ayahListView.setSelection(this.lastReadingAyah - 1);
            }
        }
    }

    private void updateTextProgress(float progress) {
        this.progressView.updateProgress(progress);
    }

    private void updateTitleWithSurahNumber(int firstVisibleItem, int lastVisibleItem) {
        View firstView = this.ayahListView.getChildAt(0);
        View lastView = this.ayahListView.getChildAt(this.ayahListView.getChildCount() - 1);

        if (firstView == null || lastView == null || this.ayahListView.getMeasuredHeight() == 0) {
            return;
        }

        int listViewHeight = this.ayahListView.getMeasuredHeight();
        float firstViewYPosition = firstView.getY();
        float lastViewYPosition = lastView.getY();

        float firstItemRemainingVisibleHeight = firstView.getMeasuredHeight() + firstViewYPosition;
        float lastItemRemainingVisibleHeight = listViewHeight - lastViewYPosition;

        boolean isFirstItemVisible = firstItemRemainingVisibleHeight > this.revealThreshold;
        boolean isLastItemVisible = lastItemRemainingVisibleHeight > this.revealThreshold;

        if (!isFirstItemVisible) {
            firstView = this.ayahListView.getChildAt(1);
        }

        if (!isLastItemVisible) {
            lastView = this.ayahListView.getChildAt(this.ayahListView.getChildCount() - 2);
        }

        if (firstView instanceof AyahView && lastView instanceof AyahView) {
            AyahView firstAyahView = (AyahView) firstView;
            AyahView lastAyahView = (AyahView) lastView;
            final int firstAyahNumber = firstAyahView.getAyahViewModel().ayahNumber;
            final int lastAyahNumber = lastAyahView.getAyahViewModel().ayahNumber;

            if (this.firstAyahNumber == firstAyahNumber && this.lastAyahNumber == lastAyahNumber) {
                return;
            }

            updateToolbarTitle(this.currentSurah.getNameInLatin(), this.currentSurah.getNumber(),
                    firstAyahNumber, lastAyahNumber);
        } else if (firstView instanceof BasmalahView && lastView instanceof AyahView) {
            // Since the possibility BasmallahView only searchable only on topmost we only lookup
            // for firstView and check the view after
            AyahView firstAyahView = (AyahView) this.ayahListView.getChildAt(1);
            AyahView lastAyahView = (AyahView) lastView;

            final int firstAyahNumber = firstAyahView.getAyahViewModel().ayahNumber;
            final int lastAyahNumber = lastAyahView.getAyahViewModel().ayahNumber;

            if (this.firstAyahNumber == firstAyahNumber && this.lastAyahNumber == lastAyahNumber) {
                return;
            }

            updateToolbarTitle(this.currentSurah.getNameInLatin(), this.currentSurah.getNumber(),
                    firstAyahNumber, lastAyahNumber);
        }
    }

    private void updateToolbarTitle(String surahNameInLatin, int surahNumber, int firstAyahNumber, int lastAyahNumber) {
        if (firstAyahNumber < 0 && lastAyahNumber < 0) {
            this.setToolbarTitle("QS. " + surahNameInLatin + " [" + surahNumber + "]");
            return;
        }

        if (firstAyahNumber == lastAyahNumber) {
            this.setToolbarTitle("QS. " + surahNameInLatin + " [" + surahNumber + "]: " + firstAyahNumber);
        } else {
            this.setToolbarTitle("QS. " + surahNameInLatin + " [" + surahNumber + "]: "
                    + firstAyahNumber + " - " + lastAyahNumber);
        }
    }

    private boolean tryToRestoreFetchSurahDetailUseCase() {
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
        useCase.setArguments(updateWith);
        useCase.setCallback(this.putDayNightPreferenceCallback);
        useCase.run();
    }

    private void createAndRunPutBookmarkUseCase(Bookmark bookmark) {
        PutBookmarkUseCase useCase = UseCaseProvider.createUseCase(PutBookmarkUseCase.class);
        useCase.setArguments(bookmark);
        useCase.setCallback(this.putBookmarkCallback);
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

    private void unregisterAndClearPutBookmarkUseCaseCallback() {
        PutBookmarkUseCase useCase = UseCaseProvider.getUseCase(PutBookmarkUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }

        UseCaseProvider.clearUseCase(PutBookmarkUseCase.class);
    }

    private void putBookmark(SelectedAyah selectedAyah) {
        final Bookmark bookmark = new Bookmark(
                selectedAyah.getSurah().getNumber(),
                selectedAyah.getSurah().getName(),
                selectedAyah.getSurah().getNameInLatin(),
                selectedAyah.getAyahNumber()
        );

        createAndRunPutBookmarkUseCase(bookmark);
    }

    private void readTafsir(SelectedAyah selectedAyah) {
        final String tafsir = this.currentSurahDetail.getSurahTafsir().getContents().get(selectedAyah.getAyahNumber());
        final String name = this.currentSurahDetail.getSurahTafsir().getName();
        final String source = this.currentSurahDetail.getSurahTafsir().getSource();
        if (tafsir != null && tafsir.length() > 0) {
            final SelectedTafsir selectedTafsir = new SelectedTafsir(
                    selectedAyah.getSurah(), selectedAyah.getAyahNumber(), tafsir, name, source);

            DialogUtil.showDialog(this, ReadTafsirDialog.class, selectedTafsir);
        }
    }

    private void clearSurahDetailUseCase() {
        UseCaseProvider.clearUseCase(FetchSurahDetailUseCase.class);
    }

    private void clearView() {
        this.ayahViewTypeList.clear();
        this.ayahDetailAdapter.clear();
    }

    private void updateViewStateLoading() {
        this.progressView.setVisibility(View.VISIBLE);
        this.ayahListView.setVisibility(View.GONE);
        this.retryView.setVisibility(View.GONE);
    }

    private void updateViewStateRetry() {
        this.progressView.setVisibility(View.GONE);
        this.ayahListView.setVisibility(View.GONE);
        this.retryView.setVisibility(View.VISIBLE);
    }

    private void updateViewStateComplete() {
        this.progressView.setVisibility(View.GONE);
        this.ayahListView.setVisibility(View.VISIBLE);
        this.retryView.setVisibility(View.GONE);
    }

    private void openAyahDetailDialog(AyahDetailViewType.AyahViewModel ayahViewModel) {
        DialogUtil.showDialog(
                this,
                AyahDetailDialog.class,
                new SelectedAyah(
                        this.currentSurah,
                        ayahViewModel.ayahNumber
                )
        );
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
        private boolean isFailedToGetSurahDetail = false;

        public SurahDetailViewState(Parcel source, ClassLoader loader) {
            super(source);

            this.currentSurah = source.readParcelable(Surah.class.getClassLoader());
            this.listViewState = source.readParcelable(loader);
            this.isFailedToGetSurahDetail = source.readInt() > 1;
        }

        public SurahDetailViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeParcelable(this.currentSurah, flags);
            out.writeParcelable(this.listViewState, flags);
            out.writeInt((this.isFailedToGetSurahDetail) ? 1 : 0);
        }
    }
}
