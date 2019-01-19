package id.fathonyfath.quranreader.views.surahList;

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

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.tasks.AsyncTaskHolder;
import id.fathonyfath.quranreader.tasks.FetchAllSurahTask;
import id.fathonyfath.quranreader.tasks.OnTaskListener;
import id.fathonyfath.quranreader.utils.ViewCallback;
import id.fathonyfath.quranreader.views.common.ProgressView;

public class SurahListView extends FrameLayout implements ViewCallback {

    private final List<Surah> surahList = new ArrayList<>();

    private final AdapterView.OnItemClickListener onSurahItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (SurahListView.this.onViewEventListener != null) {
                Surah selectedSurah = SurahListView.this.surahAdapter.getItem(position);
                SurahListView.this.onViewEventListener.onSurahSelected(selectedSurah);
            }
        }
    };

    private final OnTaskListener<List<Surah>> fetchAllSurahCallback = new OnTaskListener<List<Surah>>() {
        @Override
        public void onProgress(float progress) {
            updateTextProgress(progress);
        }

        @Override
        public void onFinished(List<Surah> result) {
            if (result != null) {
                updateSurahList(result);
            } else {
                // TODO: 19/01/19 Handle if network request failed.
            }

            clearFetchAllSurahTask();
        }
    };

    private final ListView surahListView;
    private final SurahAdapter surahAdapter;

    private final ProgressView progressView;

    private final FetchAllSurahTask.Factory fetchAllSurahTaskFactory;

    private OnViewEventListener onViewEventListener;

    public SurahListView(Context context, FetchAllSurahTask.Factory fetchAllSurahTaskFactory) {
        super(context);

        this.fetchAllSurahTaskFactory = fetchAllSurahTaskFactory;

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
    public void onResume() {
        this.surahListView.setOnItemClickListener(onSurahItemClickListener);

        if (this.surahList.isEmpty()) {
            registerTaskCallbacks();
            fetchAllSurahs();
        }
    }

    @Override
    public void onPause() {
        this.surahListView.setOnItemClickListener(null);

        clearTaskCallbacks();
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
        this.progressView.setVisibility(View.GONE);

        addView(this.progressView);
    }

    private void fetchAllSurahs() {
        this.progressView.setVisibility(View.VISIBLE);

        if (AsyncTaskHolder.fetchAllSurahTaskInstance == null) {
            AsyncTaskHolder.fetchAllSurahTaskInstance = this.fetchAllSurahTaskFactory.create();
            AsyncTaskHolder.fetchAllSurahTaskInstance.setOnTaskListener(this.fetchAllSurahCallback);
            AsyncTaskHolder.fetchAllSurahTaskInstance.execute();
        }
    }

    private void updateSurahList(List<Surah> surahList) {
        this.progressView.setVisibility(View.GONE);

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

    private void clearFetchAllSurahTask() {
        if (AsyncTaskHolder.fetchAllSurahTaskInstance != null) {
            AsyncTaskHolder.fetchAllSurahTaskInstance.setOnTaskListener(null);
            AsyncTaskHolder.fetchAllSurahTaskInstance = null;
        }
    }

    private void registerTaskCallbacks() {
        if (AsyncTaskHolder.fetchAllSurahTaskInstance != null) {
            AsyncTaskHolder.fetchAllSurahTaskInstance.setOnTaskListener(this.fetchAllSurahCallback);
        }
    }

    private void clearTaskCallbacks() {
        if (AsyncTaskHolder.fetchAllSurahTaskInstance != null) {
            AsyncTaskHolder.fetchAllSurahTaskInstance.setOnTaskListener(null);
        }
    }

    private static class SurahListViewState extends BaseSavedState {

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
    }

    public interface OnViewEventListener {
        void onSurahSelected(Surah selectedSurah);
    }
}
