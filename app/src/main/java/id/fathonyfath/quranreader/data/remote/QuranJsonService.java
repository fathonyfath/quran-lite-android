package id.fathonyfath.quranreader.data.remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import id.fathonyfath.quranreader.data.remote.models.SurahResponse;

public class QuranJsonService {

    private final String BASE_URL = "https://fathonyfath.github.io/quran-json/surah/";

    public Map<Integer, SurahResponse> getSurahIndex() {
        final Map<Integer, SurahResponse> response = new TreeMap<>();
        try {
            String httpResponse = doGetRequest(BASE_URL + "index.json");
            JSONObject responseJson = new JSONObject(httpResponse);
            JSONArray keys = responseJson.names();

            for (int i = 0; i < keys.length(); i++) {
                Integer key = keys.getInt(i);
                JSONObject value = responseJson.getJSONObject(String.valueOf(key));
                response.put(key, parseJSONObjectToSurahResponse(value));
            }
        } catch (JSONException ignored) {

        }
        return response;
    }

    public SurahResponse getSurahDetailAtNumber(int surahNumber) {
        try {
            String httpResponse = doGetRequest(BASE_URL + surahNumber + ".json");
            JSONObject responseJson = new JSONObject(httpResponse);

        } catch (JSONException ignored) {

        }
        return null;
    }

    private SurahResponse parseJSONObjectToSurahResponse(JSONObject jsonObject) throws JSONException {
        final SurahResponse surahResponse = new SurahResponse();
        surahResponse.number = jsonObject.getInt("number");
        surahResponse.name = jsonObject.getString("name");
        surahResponse.name_latin = jsonObject.getString("name_latin");
        surahResponse.number_of_ayah = jsonObject.getInt("number_of_ayah");
        return surahResponse;
    }

    private String doGetRequest(String url) {
        HttpsURLConnection urlConnection = null;
        try {
            URL indexUrl = new URL(url);
            urlConnection = (HttpsURLConnection) indexUrl.openConnection();
            urlConnection.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            return readStream(in);
        } catch (MalformedURLException ignored) {

        } catch (IOException ignored) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return "";
    }

    private String readStream(InputStream is) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}
