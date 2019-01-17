package id.fathonyfath.quranreader.data.remote;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import id.fathonyfath.quranreader.data.remote.models.SurahDetailResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTafsirResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTafsirSourceResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTafsirsResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTranslationResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTranslationsResponse;

public class QuranJsonService {

    private final String BASE_URL = "https://fathonyfath.github.io/quran-json/surah/";

    private OnDownloadProgressListener onDownloadProgressListener;

    public Map<Integer, SurahResponse> getSurahIndex() {
        try {
            String httpResponse = doGetRequest(BASE_URL + "index.json");
            JSONObject responseJson = new JSONObject(httpResponse);
            return parseJSONObjectToMapOfIntegerSurahResponse(responseJson);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public void setOnDownloadProgressListener(OnDownloadProgressListener onDownloadProgressListener) {
        this.onDownloadProgressListener = onDownloadProgressListener;
    }

    public Pair<String, SurahDetailResponse> getSurahDetailAtNumber(int surahNumber) {
        try {
            String httpResponse = doGetRequest(BASE_URL + surahNumber + ".json");
            JSONObject responseJson = new JSONObject(httpResponse);
            SurahDetailResponse surahDetail = parseJSONObjectToSurahDetailResponse(responseJson.getJSONObject(String.valueOf(surahNumber)));
            return new Pair<>(String.valueOf(surahNumber), surahDetail);
        } catch (JSONException ignored) {

        }
        return null;
    }

    private Map<Integer, SurahResponse> parseJSONObjectToMapOfIntegerSurahResponse(JSONObject jsonObject) throws JSONException {
        final Map<Integer, SurahResponse> response = new TreeMap<>();

        JSONArray keys = jsonObject.names();
        for (int i = 0; i < keys.length(); i++) {
            Integer key = keys.getInt(i);
            JSONObject value = jsonObject.getJSONObject(String.valueOf(key));
            response.put(key, parseJSONObjectToSurahResponse(value));
        }

        return response;
    }

    private SurahResponse parseJSONObjectToSurahResponse(JSONObject index) throws JSONException {
        final SurahResponse surahResponse = new SurahResponse();
        surahResponse.number = index.getInt("number");
        surahResponse.name = index.getString("name");
        surahResponse.name_latin = index.getString("name_latin");
        surahResponse.number_of_ayah = index.getInt("number_of_ayah");
        return surahResponse;
    }

    private SurahDetailResponse parseJSONObjectToSurahDetailResponse(JSONObject jsonObject) throws JSONException {
        final SurahDetailResponse surahDetailResponse = new SurahDetailResponse();
        surahDetailResponse.number = jsonObject.getInt("number");
        surahDetailResponse.name = jsonObject.getString("name");
        surahDetailResponse.name_latin = jsonObject.getString("name_latin");
        surahDetailResponse.number_of_ayah = jsonObject.getInt("number_of_ayah");
        surahDetailResponse.text = parseJSONObjectToMapOfIntegerString(jsonObject.getJSONObject("text"));
        surahDetailResponse.translations = parseJSONObjectToSurahTranslationsResponse(jsonObject.getJSONObject("translations"));
        surahDetailResponse.tafsir = parseJSONObjectToSurahTafsirsResponse(jsonObject.getJSONObject("tafsir"));
        return surahDetailResponse;
    }

    private SurahTranslationsResponse parseJSONObjectToSurahTranslationsResponse(JSONObject translations) throws JSONException {
        final SurahTranslationsResponse surahTranslations = new SurahTranslationsResponse();

        final Map<String, SurahTranslationResponse> translationsMap = new TreeMap<>();

        JSONArray keys = translations.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = translations.getJSONObject(String.valueOf(key));
            translationsMap.put(key, parseJSONOBjectToSurahTranslationResponse(value));
        }

        surahTranslations.translations = translationsMap;

        return surahTranslations;
    }

    private SurahTranslationResponse parseJSONOBjectToSurahTranslationResponse(JSONObject value) throws JSONException {
        final SurahTranslationResponse surahTranslation = new SurahTranslationResponse();

        surahTranslation.name = value.getString("name");
        surahTranslation.text = parseJSONObjectToMapOfIntegerString(value.getJSONObject("text"));

        return surahTranslation;
    }

    private SurahTafsirsResponse parseJSONObjectToSurahTafsirsResponse(JSONObject tafsir) throws JSONException {
        final SurahTafsirsResponse surahTafsirs = new SurahTafsirsResponse();
        final Map<String, SurahTafsirResponse> tafsirsMap = new TreeMap<>();

        JSONArray keys = tafsir.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = tafsir.getJSONObject(String.valueOf(key));
            tafsirsMap.put(key, parseJSONObjectToSurahTafsirResponse(value));
        }

        surahTafsirs.tafsir = tafsirsMap;

        return surahTafsirs;
    }

    private SurahTafsirResponse parseJSONObjectToSurahTafsirResponse(JSONObject tafsirSources) throws JSONException {
        final SurahTafsirResponse surahTafsirResponse = new SurahTafsirResponse();
        final Map<String, SurahTafsirSourceResponse> tafsirMap = new TreeMap<>();

        JSONArray keys = tafsirSources.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject value = tafsirSources.getJSONObject(String.valueOf(key));
            tafsirMap.put(key, parseJSONObjectToSurahTafsirSourceResponse(value));
        }

        surahTafsirResponse.sources = tafsirMap;

        return surahTafsirResponse;
    }

    private SurahTafsirSourceResponse parseJSONObjectToSurahTafsirSourceResponse(JSONObject source) throws JSONException {
        final SurahTafsirSourceResponse tafsirSource = new SurahTafsirSourceResponse();

        tafsirSource.name = source.getString("name");
        tafsirSource.source = source.getString("source");
        tafsirSource.text = parseJSONObjectToMapOfIntegerString(source.getJSONObject("text"));

        return tafsirSource;
    }

    private Map<Integer, String> parseJSONObjectToMapOfIntegerString(JSONObject text) throws JSONException {
        final Map<Integer, String> response = new TreeMap<>();

        JSONArray keys = text.names();
        for (int i = 0; i < keys.length(); i++) {
            Integer key = keys.getInt(i);
            String value = text.getString(String.valueOf(key));
            response.put(key, value);
        }

        return response;
    }

    private String doGetRequest(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL indexUrl = new URL(url);
            urlConnection = (HttpURLConnection) indexUrl.openConnection();
            urlConnection.setRequestProperty("Accept-Encoding", "identity");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            int contentLength = urlConnection.getContentLength();
            int totalRead = 0;

            String[] responseHeader = urlConnection.getContentType().split(";");
            String charset = "";

            for (String value : responseHeader) {
                value = value.trim();
                if (value.toLowerCase().startsWith("charset=")) {
                    charset = value.substring("charset=".length());
                    break;
                }
            }
            if (charset.equals("")) charset = "utf-8";

            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            final StringBuilder sb = new StringBuilder();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                if (this.onDownloadProgressListener != null) {
                    this.onDownloadProgressListener.onDownloadProgress(totalRead, contentLength);
                }
                Log.d("QuranJsonService", "Current: " + totalRead + " Total: " + contentLength);
            }

            stream.close();

            byte[] contentByteArray = outputStream.toByteArray();
            sb.append(new String(contentByteArray, 0, contentByteArray.length, charset.toUpperCase()));

            return sb.toString();
        } catch (MalformedURLException ignored) {

        } catch (IOException ignored) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return "";
    }
}
