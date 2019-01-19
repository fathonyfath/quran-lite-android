package id.fathonyfath.quranreader.views.surahDetail;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
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

public class SurahDetailView extends FrameLayout implements ViewCallback {

    private final List<AyahDetailViewType> ayahViewTypeList = new ArrayList<>();

    private final ListView ayahListView;
    private final AyahDetailAdapter ayahDetailAdapter;

    private final FetchSurahDetailTask.Factory fetchSurahDetailTaskFactory;

    public SurahDetailView(Context context, FetchSurahDetailTask.Factory fetchSurahDetailTaskFactory) {
        super(context);

        this.fetchSurahDetailTaskFactory = fetchSurahDetailTaskFactory;

        setId(Res.Id.surahDetailView);

        this.ayahListView = new ListView(context);
        this.ayahDetailAdapter = new AyahDetailAdapter(context, this.ayahViewTypeList);

        initConfiguration();
        initView();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        clearView();
    }

    public void updateView(Surah selectedSurah) {
        final FetchSurahDetailTask fetchSurahDetailTask = this.fetchSurahDetailTaskFactory.create();

        fetchSurahDetailTask.setOnTaskListener(new OnTaskListener<SurahDetail>() {
            @Override
            public void onProgress(float progress) {

            }

            @Override
            public void onFinished(SurahDetail result) {
                ayahViewTypeList.clear();

                for (Map.Entry<Integer, String> entry : result.getContents().entrySet()) {
                    String translation = result.getSurahTranslation().getContents().get(entry.getKey());
                    if (translation == null) {
                        translation = "";
                    }
                    ayahViewTypeList.add(new AyahDetailViewType.AyahViewModel(entry.getKey(), entry.getValue(), translation));
                }

                ayahDetailAdapter.notifyDataSetChanged();
            }
        });
        fetchSurahDetailTask.execute(selectedSurah);
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
    }

    private void clearView() {
        this.ayahViewTypeList.clear();
        this.ayahDetailAdapter.clear();
    }
}
