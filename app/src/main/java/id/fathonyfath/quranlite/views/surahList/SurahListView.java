package id.fathonyfath.quranlite.views.surahList;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.fathonyfath.quranlite.Res;
import id.fathonyfath.quranlite.models.Surah;
import id.fathonyfath.quranlite.useCase.FetchAllSurahUseCase;
import id.fathonyfath.quranlite.useCase.UseCaseCallback;
import id.fathonyfath.quranlite.useCase.UseCaseProvider;
import id.fathonyfath.quranlite.utils.viewLifecycle.ViewCallback;
import id.fathonyfath.quranlite.views.common.ProgressView;

public class SurahListView extends FrameLayout implements ViewCallback {

    private final List<Surah> surahList = new ArrayList<>();
    private final ListView surahListView;
    private final SurahAdapter surahAdapter;
    private final ProgressView progressView;
    private final UseCaseCallback<List<Surah>> fetchAllSurahCallback = new UseCaseCallback<List<Surah>>() {
        @Override
        public void onProgress(float progress) {
            updateTextProgress(progress);
        }

        @Override
        public void onResult(List<Surah> data) {
            // Don't forget to do some cleanups
            unregisterUseCaseCallback();
            clearUseCase();

            updateSurahList(data);
        }

        @Override
        public void onError(Throwable throwable) {
            // Don't forget to do some cleanups
            unregisterUseCaseCallback();
            clearUseCase();
        }
    };
    private OnViewEventListener onViewEventListener;
    private final AdapterView.OnItemClickListener onSurahItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (SurahListView.this.onViewEventListener != null) {
                Surah selectedSurah = SurahListView.this.surahAdapter.getItem(position);
                SurahListView.this.onViewEventListener.onSurahSelected(selectedSurah);
            }
        }
    };

    public SurahListView(Context context) {
        super(context);

        setId(Res.Id.surahListView);

        this.surahListView = new ListView(getContext());
        this.surahAdapter = new SurahAdapter(getContext(), this.surahList);

        this.progressView = new ProgressView(getContext());

        initConfiguration();
        initView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SurahListViewState viewState = new SurahListViewState(super.onSaveInstanceState());
        viewState.surahList = this.surahList;
        return viewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SurahListViewState viewState = (SurahListViewState) state;
        super.onRestoreInstanceState(viewState.getSuperState());
        restoreSurahList(viewState.surahList);
    }

    @Override
    public void onStart() {
        this.progressView.setVisibility(View.GONE);
        updateTextProgress(0f);
    }

    @Override
    public void onResume() {
        this.progressView.setVisibility(View.GONE);

        this.surahListView.setOnItemClickListener(onSurahItemClickListener);

        if (this.surahList.isEmpty()) {
            this.progressView.setVisibility(View.VISIBLE);

            if (!tryToRestoreUseCase()) {
                createAndRunUseCase();
            }
        }
    }

    @Override
    public void onPause() {
        this.surahListView.setOnItemClickListener(null);

        unregisterUseCaseCallback();
    }

    @Override
    public void onStop() {
        clearUseCase();
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

        addView(this.progressView);
    }

    private boolean tryToRestoreUseCase() {
        FetchAllSurahUseCase useCase = UseCaseProvider.getUseCase(FetchAllSurahUseCase.class);
        if (useCase != null) {
            useCase.setCallback(this.fetchAllSurahCallback);
            return true;
        }
        return false;
    }

    private void createAndRunUseCase() {
        FetchAllSurahUseCase useCase = UseCaseProvider.createUseCase(FetchAllSurahUseCase.class);
        useCase.setCallback(this.fetchAllSurahCallback);
        useCase.run();
    }

    private void unregisterUseCaseCallback() {
        FetchAllSurahUseCase useCase = UseCaseProvider.getUseCase(FetchAllSurahUseCase.class);
        if (useCase != null) {
            useCase.setCallback(null);
        }
    }

    private void clearUseCase() {
        UseCaseProvider.clearUseCase(FetchAllSurahUseCase.class);
    }

    private void updateSurahList(List<Surah> surahList) {
        this.progressView.setVisibility(View.GONE);
        updateTextProgress(0f);

        this.surahAdapter.clear();
        this.surahAdapter.addAll(surahList);
        this.surahAdapter.notifyDataSetChanged();
    }

    private void updateTextProgress(float progress) {
        this.progressView.updateProgress(progress);
    }

    private void restoreSurahList(List<Surah> surahList) {
        this.surahList.clear();
        this.surahList.addAll(surahList);
    }

    public interface OnViewEventListener {
        void onSurahSelected(Surah selectedSurah);
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

        public SurahListViewState(Parcel source, ClassLoader loader) {
            super(source);

            int size = source.readInt();
            Surah[] surahArray = new Surah[size];
            source.readTypedArray(surahArray, Surah.CREATOR);

            this.surahList.clear();
            this.surahList.addAll(Arrays.asList(surahArray));
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
        }
    }
}
