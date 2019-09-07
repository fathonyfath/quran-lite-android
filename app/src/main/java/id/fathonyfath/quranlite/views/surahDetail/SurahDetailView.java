package id.fathonyfath.quranlite.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.fathonyfath.quranlite.Res;
import id.fathonyfath.quranlite.models.Surah;
import id.fathonyfath.quranlite.models.SurahDetail;
import id.fathonyfath.quranlite.useCase.FetchSurahDetailUseCase;
import id.fathonyfath.quranlite.useCase.UseCaseCallback;
import id.fathonyfath.quranlite.useCase.UseCaseProvider;
import id.fathonyfath.quranlite.utils.ViewUtil;
import id.fathonyfath.quranlite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quranlite.views.common.ProgressView;
import id.fathonyfath.quranlite.views.common.WrapperView;

public class SurahDetailView extends WrapperView implements ViewCallback {

    private final List<AyahDetailViewType> ayahViewTypeList = new ArrayList<>();
    private final ListView ayahListView;
    private final AyahDetailAdapter ayahDetailAdapter;
    private final ProgressView progressView;
    private Surah currentSurah;
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
            unregisterUseCaseCallback();
            clearUseCase();

            processSurahDetail(data);
        }

        @Override
        public void onError(Throwable throwable) {
            // Don't forget to do some cleanups
            unregisterUseCaseCallback();
            clearUseCase();
        }
    };

    public SurahDetailView(Context context) {
        super(context);

        setId(Res.Id.surahDetailView);

        this.ayahListView = new ListView(context);
        this.ayahListView.setId(Res.Id.surahDetailView_ayahListView);
        this.ayahDetailAdapter = new AyahDetailAdapter(context, this.ayahViewTypeList);

        this.progressView = new ProgressView(getContext());

        this.setElevationAlpha(0.1f);

        initConfiguration();
        initView();
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

        if (this.currentSurah != null) {
            this.setToolbarTitle("QS. " + this.currentSurah.getNameInLatin() + " [" + this.currentSurah.getNumber() + "]");
        }

    }

    @Override
    public void onResume() {
        if (this.currentSurah != null) {
            this.progressView.setVisibility(View.VISIBLE);

            if (!tryToRestoreUseCase()) {
                createAndRunUseCase();
            }
        } else {
            ViewUtil.onBackPressed(this);
        }
    }

    @Override
    public void onPause() {
        clearView();
        unregisterUseCaseCallback();
    }

    @Override
    public void onStop() {
        clearUseCase();
    }

    public void setState(Surah selectedSurah) {
        this.currentSurah = selectedSurah;
        this.newPage = true;
    }

    public String getSurahName() {
        if (this.currentSurah != null) {
            return this.currentSurah.getNameInLatin();
        }
        return "";
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

    private boolean tryToRestoreUseCase() {
        FetchSurahDetailUseCase useCase = UseCaseProvider.getUseCase(FetchSurahDetailUseCase.class);
        if (useCase != null) {
            useCase.setCallback(this.fetchSurahDetailCallback);
            return true;
        }
        return false;
    }

    private void createAndRunUseCase() {
        FetchSurahDetailUseCase useCase = UseCaseProvider.createUseCase(FetchSurahDetailUseCase.class);
        useCase.setCallback(this.fetchSurahDetailCallback);
        useCase.setArguments(this.currentSurah);
        useCase.run();
    }

    private void unregisterUseCaseCallback() {
        FetchSurahDetailUseCase useCase = UseCaseProvider.getUseCase(FetchSurahDetailUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }
    }

    private void clearUseCase() {
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
