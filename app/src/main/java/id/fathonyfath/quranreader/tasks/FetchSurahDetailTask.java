package id.fathonyfath.quranreader.tasks;

import android.os.AsyncTask;

import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.models.SurahDetail;

public class FetchSurahDetailTask extends AsyncTask<Surah, Void, SurahDetail> {

    private final QuranRepository quranRepository;

    private OnTaskFinishedListener<SurahDetail> onFinishCallback;

    public FetchSurahDetailTask(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
    }

    public void setOnFinishCallback(OnTaskFinishedListener<SurahDetail> onFinishCallback) {
        this.onFinishCallback = onFinishCallback;
    }

    @Override
    protected SurahDetail doInBackground(Surah... surahs) {
        if (surahs != null) {
            return this.quranRepository.fetchSurahDetail(surahs[0]);
        }
        return null;
    }

    @Override
    protected void onPostExecute(SurahDetail s) {
        super.onPostExecute(s);

        if (onFinishCallback != null) {
            onFinishCallback.onFinished(s);
        }
    }
}
