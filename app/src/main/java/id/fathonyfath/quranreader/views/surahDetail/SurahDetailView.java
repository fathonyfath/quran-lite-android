package id.fathonyfath.quranreader.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.models.SurahDetail;
import id.fathonyfath.quranreader.tasks.FetchSurahDetailTask;
import id.fathonyfath.quranreader.tasks.OnTaskListener;
import id.fathonyfath.quranreader.utils.ViewCallback;

public class SurahDetailView extends FrameLayout implements ViewCallback {

    private final List<String> ayahList = new ArrayList<>();

    private final ListView ayahListView;
    private final AyahAdapter ayahAdapter;

    private final FetchSurahDetailTask fetchSurahDetailTask;

    public SurahDetailView(Context context, FetchSurahDetailTask fetchSurahDetailTask) {
        super(context);

        this.fetchSurahDetailTask = fetchSurahDetailTask;

        setId(Res.Id.surahDetailView);

        this.ayahListView = new ListView(context);
        this.ayahAdapter = new AyahAdapter(context, this.ayahList);

        initConfiguration();
        initView();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    public void updateView(Surah selectedSurah) {
        this.fetchSurahDetailTask.setOnTaskListener(new OnTaskListener<SurahDetail>() {
            @Override
            public void onProgress(float progress) {
                Log.d("SurahDetailView", "Progress; " + progress);
            }

            @Override
            public void onFinished(SurahDetail result) {
                ayahList.clear();
                ayahList.addAll(result.getContents().values());
                ayahAdapter.notifyDataSetChanged();
            }
        });
        this.fetchSurahDetailTask.execute(selectedSurah);
    }

    private void initConfiguration() {
        setBackgroundColor(Color.WHITE);
    }

    private void initView() {
        this.ayahListView.setAdapter(this.ayahAdapter);
        this.ayahListView.setDivider(null);

        addView(this.ayahListView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }
}
