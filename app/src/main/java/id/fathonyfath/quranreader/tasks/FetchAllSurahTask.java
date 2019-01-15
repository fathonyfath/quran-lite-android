package id.fathonyfath.quranreader.tasks;

import android.os.AsyncTask;

import java.util.List;

import id.fathonyfath.quranreader.data.QuranRepository;
import id.fathonyfath.quranreader.models.Surah;

public class FetchAllSurahTask extends AsyncTask<Void, Void, List<Surah>> {

    private final QuranRepository quranRepository;

    private OnTaskFinishedListener<List<Surah>> onFinishCallback;

    public FetchAllSurahTask(QuranRepository quranRepository) {
        this.quranRepository = quranRepository;
    }

    public void setOnFinishCallbackListener(OnTaskFinishedListener<List<Surah>> onFinishCallback) {
        this.onFinishCallback = onFinishCallback;
    }

    public void removeCallbackListener() {
        this.onFinishCallback = null;
    }

    @Override
    protected List<Surah> doInBackground(Void... voids) {
        return this.quranRepository.fetchAllSurah();
    }

    @Override
    protected void onPostExecute(List<Surah> s) {
        super.onPostExecute(s);

        if (this.onFinishCallback != null) {
            this.onFinishCallback.onFinished(s);
        }
    }
}
