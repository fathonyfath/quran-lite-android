package id.fathonyfath.quranreader.views.surahList;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.tasks.FetchAllSurahTask;
import id.fathonyfath.quranreader.tasks.OnTaskListener;
import id.fathonyfath.quranreader.utils.ViewCallback;
import id.fathonyfath.quranreader.views.common.ProgressView;

public class SurahListView extends FrameLayout implements ViewCallback {

    private final Hashtable<Integer, Integer> listViewItemHeights = new Hashtable<>();
    private final List<Surah> surahList = new ArrayList<>();

    private final AbsListView.OnScrollListener scrollChangeListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            SurahListView.this.triggerSurahListScroll();
        }
    };

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
            updateSurahList(result);
        }
    };

    private final ListView surahListView;
    private final SurahAdapter surahAdapter;

    private final ProgressView progressView;

    private final FetchAllSurahTask.Factory fetchAllSurahTaskFactory;
    private FetchAllSurahTask fetchAllSurahTask;

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
        viewState.listViewItemHeights = this.listViewItemHeights;
        viewState.surahList = this.surahList;
        return viewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SurahListViewState viewState = (SurahListViewState) state;
        super.onRestoreInstanceState(viewState.getSuperState());
        restoreListViewItemHeights(viewState.listViewItemHeights);
        restoreSurahList(viewState.surahList);
    }

    @Override
    public void onResume() {
        this.surahListView.setOnItemClickListener(onSurahItemClickListener);
        if (this.surahList.isEmpty()) {
            fetchAllSurahs();
        }
    }

    @Override
    public void onPause() {
        this.surahListView.setOnItemClickListener(null);

        clearTask();
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
        this.surahListView.setOnScrollListener(scrollChangeListener);

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

        clearTask();
        this.fetchAllSurahTask = this.fetchAllSurahTaskFactory.create();

        this.fetchAllSurahTask.setOnTaskListener(fetchAllSurahCallback);
        this.fetchAllSurahTask.execute();
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

    private void triggerSurahListScroll() {
        if (this.surahListView.getChildCount() > 0) {
            int scrollY = getScrollYListView();
            if (this.onViewEventListener != null) {
                this.onViewEventListener.onSurahListScroll(scrollY);
            }
        }
    }

    private int getScrollYListView() {
        View child = this.surahListView.getChildAt(0);
        if (child == null) return 0;

        int scrollY = -child.getTop();

        this.listViewItemHeights.put(this.surahListView.getFirstVisiblePosition(), child.getHeight());

        for (int i = 0; i < this.surahListView.getFirstVisiblePosition(); ++i) {
            Integer height = listViewItemHeights.get(i);

            if (height != null)
                scrollY += height;
        }

        return scrollY;
    }

    private void restoreListViewItemHeights(Hashtable<Integer, Integer> listViewItemHeights) {
        this.listViewItemHeights.clear();
        this.listViewItemHeights.putAll(listViewItemHeights);
    }

    private void restoreSurahList(List<Surah> surahList) {
        this.surahList.clear();
        this.surahList.addAll(surahList);
    }

    private void clearTask() {
        if (this.fetchAllSurahTask != null) {
            this.fetchAllSurahTask.cancel(true);
            this.fetchAllSurahTask.setOnTaskListener(null);
            this.fetchAllSurahTask = null;
        }
    }

    private static class SurahListViewState extends BaseSavedState {

        private Hashtable<Integer, Integer> listViewItemHeights = new Hashtable<>();
        private List<Surah> surahList = new ArrayList<>();

        public SurahListViewState(Parcel source, ClassLoader loader) {
            super(source);

            Serializable serializable = source.readSerializable();

            if (serializable instanceof Hashtable) {
                Hashtable hashTable = (Hashtable) serializable;
                for (Object iterate : hashTable.entrySet()) {
                    if (iterate instanceof Map.Entry) {
                        Map.Entry entry = (Map.Entry) iterate;
                        if (entry.getKey() instanceof Integer && entry.getValue() instanceof Integer) {
                            Integer key = (Integer) entry.getKey();
                            Integer value = (Integer) entry.getValue();
                            listViewItemHeights.put(key, value);
                        }
                    }
                }
            }

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
            out.writeSerializable(this.listViewItemHeights);

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
        void onSurahListScroll(float scrollY);
        void onSurahSelected(Surah selectedSurah);
    }
}
