package id.fathonyfath.quranlite.data.source.network;

import org.json.JSONException;
import org.json.JSONObject;

import id.fathonyfath.quranlite.utils.network.NetworkHelper;

public class QuranNetworkSource {

    private final String BASE_URL = "https://fathonyfath.github.io/quran-json/surah/";

    public JSONObject getSurahIndex(NetworkHelper.CancelSignal cancelSignal,
                                    NetworkHelper.ProgressListener progressListener) {
        String result = NetworkHelper.doGetRequest(BASE_URL + "index.json", cancelSignal, progressListener);
        try {
            if (result != null) {
                return new JSONObject(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getSurahDetailAtNumber(int surahNumber,
                                             NetworkHelper.CancelSignal cancelSignal,
                                             NetworkHelper.ProgressListener progressListener) {
        String result = NetworkHelper.doGetRequest(
                BASE_URL + surahNumber + ".json",
                cancelSignal,
                progressListener);

        try {
            if (result != null) {
                final JSONObject parentJSON = new JSONObject(result);
                return parentJSON.getJSONObject(String.valueOf(surahNumber));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
