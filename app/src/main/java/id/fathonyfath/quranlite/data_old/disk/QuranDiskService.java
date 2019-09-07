package id.fathonyfath.quranlite.data_old.disk;

import android.content.Context;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import id.fathonyfath.quranlite.data_old.models.SurahDetailResponse;
import id.fathonyfath.quranlite.data_old.models.SurahResponse;
import id.fathonyfath.quranlite.data_old.transformer.SurahDetailResponseTransformer;
import id.fathonyfath.quranlite.data_old.transformer.SurahResponseTransformer;

public class QuranDiskService {

    private static final String indexFileName = "index.json";

    private final File destinationDirectory;

    public QuranDiskService(Context context) {
        this.destinationDirectory = new File(context.getFilesDir(), "contents");
        this.destinationDirectory.mkdirs();
    }

    public boolean saveSurahIndex(JSONObject jsonObject) {
        final File indexFile = new File(this.destinationDirectory, QuranDiskService.indexFileName);
        if (indexFile.exists()) {
            indexFile.delete();
        }

        return saveStringToFile(indexFile, jsonObject.toString());
    }

    public Map<Integer, SurahResponse> getSurahIndex() {
        final File indexFile = new File(this.destinationDirectory, QuranDiskService.indexFileName);
        if (!indexFile.exists()) {
            return null;
        }

        try {
            final String jsonFile = fetchStringFromFile(indexFile);

            if (jsonFile == null) {
                return null;
            }

            final JSONObject responseJson = new JSONObject(jsonFile);
            return SurahResponseTransformer.parseJSONObjectToMapOfIntegerSurahResponse(responseJson);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public boolean isSurahIndexExist() {
        final File indexFile = new File(this.destinationDirectory, QuranDiskService.indexFileName);
        return indexFile.exists();
    }

    public boolean saveSurahDetailAtNumber(int surahNumber, JSONObject jsonObject) {
        final File surahFile = new File(this.destinationDirectory, surahNumber + ".json");
        if (surahFile.exists()) {
            surahFile.delete();
        }

        return saveStringToFile(surahFile, jsonObject.toString());
    }

    public Pair<String, SurahDetailResponse> getSurahDetailAtNumber(int surahNumber) {
        final File surahFile = new File(this.destinationDirectory, surahNumber + ".json");
        if (!surahFile.exists()) {
            return null;
        }

        try {
            final String jsonFile = fetchStringFromFile(surahFile);

            if (jsonFile == null) {
                return null;
            }

            JSONObject responseJson = new JSONObject(jsonFile);
            SurahDetailResponse surahDetail = SurahDetailResponseTransformer
                    .parseJSONObjectToSurahDetailResponse(
                            responseJson.getJSONObject(String.valueOf(surahNumber))
                    );
            return new Pair<>(String.valueOf(surahNumber), surahDetail);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public boolean isSurahDetailAtNumberExist(int surahNumber) {
        final File surahFile = new File(this.destinationDirectory, surahNumber + ".json");
        return surahFile.exists();
    }

    private byte[] readInputStreamToByteArray(InputStream inputStream) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException ignored) {

        }
        return null;
    }

    private boolean saveStringToFile(File destination, String content) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(destination);
            final OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutputStream);
            streamWriter.write(content);

            streamWriter.close();
            fileOutputStream.close();

            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private String fetchStringFromFile(File source) {
        try {
            final FileInputStream indexStream = new FileInputStream(source);
            byte[] contentsByteArray = readInputStreamToByteArray(indexStream);

            if (contentsByteArray == null) {
                return null;
            }

            final String charset = "UTF-8";

            return new String(contentsByteArray, 0, contentsByteArray.length, charset);
        } catch (IOException ignored) {

        }
        return null;
    }
}
