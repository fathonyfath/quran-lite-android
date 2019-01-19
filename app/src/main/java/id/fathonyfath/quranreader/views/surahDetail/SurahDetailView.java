package id.fathonyfath.quranreader.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.models.SurahDetail;
import id.fathonyfath.quranreader.tasks.FetchSurahDetailTask;
import id.fathonyfath.quranreader.tasks.OnTaskListener;
import id.fathonyfath.quranreader.utils.ViewCallback;
import id.fathonyfath.quranreader.utils.ViewUtil;
import id.fathonyfath.quranreader.views.common.ProgressView;

public class SurahDetailView extends FrameLayout implements ViewCallback {

    private Surah selectedSurah;

    private final List<AyahDetailViewType> ayahViewTypeList = new ArrayList<>();

    private final ListView ayahListView;
    private final AyahDetailAdapter ayahDetailAdapter;

    private final ProgressView progressView;

    private final FetchSurahDetailTask.Factory fetchSurahDetailTaskFactory;
    private FetchSurahDetailTask fetchSurahDetailTask;
    private final OnTaskListener<SurahDetail> fetchSurahDetailCallback = new OnTaskListener<SurahDetail>() {
        @Override
        public void onProgress(float progress) {
            updateTextProgress(progress);
        }

        @Override
        public void onFinished(SurahDetail result) {
            processSurahDetail(result);
        }
    };

    public SurahDetailView(Context context, FetchSurahDetailTask.Factory fetchSurahDetailTaskFactory) {
        super(context);

        this.fetchSurahDetailTaskFactory = fetchSurahDetailTaskFactory;

        setId(Res.Id.surahDetailView);

        this.ayahListView = new ListView(context);
        this.ayahDetailAdapter = new AyahDetailAdapter(context, this.ayahViewTypeList);

        this.progressView = new ProgressView(getContext());

        initConfiguration();
        initView();
    }

    @Override
    public void onResume() {
        if (this.selectedSurah != null) {
            fetchSurahDetail(this.selectedSurah);
        } else {
            ViewUtil.onBackPressed(this);
        }
    }

    @Override
    public void onPause() {
        clearView();
    }

    public void setState(Surah selectedSurah) {
        this.selectedSurah = selectedSurah;
    }

    private void initConfiguration() {
        setBackgroundColor(Color.WHITE);
    }

    private void initView() {
        this.ayahListView.setAdapter(this.ayahDetailAdapter);
        this.ayahListView.setDivider(null);

        addView(this.ayahListView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        final FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressParams.gravity = Gravity.CENTER;
        this.progressView.setLayoutParams(progressParams);
        this.progressView.setVisibility(View.GONE);

        addView(this.progressView);
    }

    private void fetchSurahDetail(Surah selectedSurah) {
        clearFetchSurahDetailTask();

        this.progressView.setVisibility(View.VISIBLE);

        this.fetchSurahDetailTask = this.fetchSurahDetailTaskFactory.create();
        this.fetchSurahDetailTask.setOnTaskListener(this.fetchSurahDetailCallback);
        this.fetchSurahDetailTask.execute(selectedSurah);
    }

    private void processSurahDetail(SurahDetail surahDetail) {
        this.progressView.setVisibility(View.GONE);

        this.ayahViewTypeList.clear();

        for (Map.Entry<Integer, String> entry : surahDetail.getContents().entrySet()) {
            String translation = surahDetail.getSurahTranslation().getContents().get(entry.getKey());
            if (translation == null) {
                translation = "";
            }
            this.ayahViewTypeList.add(new AyahDetailViewType.AyahViewModel(entry.getKey(), entry.getValue(), translation));
        }

        this.ayahDetailAdapter.notifyDataSetChanged();
    }

    private void updateTextProgress(float progress) {
        this.progressView.updateProgress(progress);
    }

    private void clearFetchSurahDetailTask() {
        if (this.fetchSurahDetailTask != null) {
            this.fetchSurahDetailTask.cancel(true);
            this.fetchSurahDetailTask.setOnTaskListener(null);
            this.fetchSurahDetailTask = null;
        }
    }

    private void clearView() {
        this.ayahViewTypeList.clear();
        this.ayahDetailAdapter.clear();
    }
}
