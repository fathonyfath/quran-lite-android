package id.fathonyfath.quranreader.data.remote;

import android.util.Pair;

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

import id.fathonyfath.quranreader.data.models.SurahDetailResponse;
import id.fathonyfath.quranreader.data.models.SurahResponse;
import id.fathonyfath.quranreader.data.transformer.SurahDetailResponseTransformer;
import id.fathonyfath.quranreader.data.transformer.SurahResponseTransformer;

public class QuranJsonService {

    private final String BASE_URL = "https://fathonyfath.github.io/quran-json/surah/";

    private OnDownloadProgressListener onDownloadProgressListener;

    public Map<Integer, SurahResponse> getSurahIndex() {
        try {
            String httpResponse = doGetRequest(BASE_URL + "index.json");
            JSONObject responseJson = new JSONObject(httpResponse);
            return SurahResponseTransformer.parseJSONObjectToMapOfIntegerSurahResponse(responseJson);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public Pair<String, SurahDetailResponse> getSurahDetailAtNumber(int surahNumber) {
        try {
            String httpResponse = doGetRequest(BASE_URL + surahNumber + ".json");
            JSONObject responseJson = new JSONObject(httpResponse);
            SurahDetailResponse surahDetail = SurahDetailResponseTransformer
                    .parseJSONObjectToSurahDetailResponse(
                            responseJson.getJSONObject(String.valueOf(surahNumber))
                    );
            return new Pair<>(String.valueOf(surahNumber), surahDetail);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public void setOnDownloadProgressListener(OnDownloadProgressListener onDownloadProgressListener) {
        this.onDownloadProgressListener = onDownloadProgressListener;
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
