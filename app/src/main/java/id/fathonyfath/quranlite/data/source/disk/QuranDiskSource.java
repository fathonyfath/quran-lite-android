package id.fathonyfath.quranlite.data.source.disk;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class QuranDiskSource {

    private static final String indexFileName = "index.json";

    private final File destDir;

    public QuranDiskSource(Context context) {
        this.destDir = new File(context.getFilesDir(), "contents");
        this.destDir.mkdirs();
    }

    public JSONObject getSurahIndex() {
        final File indexFile = new File(this.destDir, QuranDiskSource.indexFileName);
        if (!indexFile.exists()) {
            return null;
        }

        try {
            final String jsonFile = readStringFromFile(indexFile);

            if (jsonFile == null) {
                return null;
            }

            return new JSONObject(jsonFile);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public JSONObject getSurahDetailAtNumber(int surahNumber) {
        final File surahFile = new File(this.destDir, surahNumber + ".json");
        if (!surahFile.exists()) {
            return null;
        }

        try {
            final String jsonFile = readStringFromFile(surahFile);

            if (jsonFile == null) {
                return null;
            }

            return new JSONObject(jsonFile);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public boolean saveSurahIndex(JSONObject jsonObject) {
        final File indexFile = new File(this.destDir, QuranDiskSource.indexFileName);
        if (indexFile.exists()) {
            indexFile.delete();
        }

        return saveStringToFile(indexFile, jsonObject.toString());
    }

    public boolean saveSurahDetailAtNumber(int surahNumber, JSONObject jsonObject) {
        final File surahFile = new File(this.destDir, surahNumber + ".json");
        if (surahFile.exists()) {
            surahFile.delete();
        }

        return saveStringToFile(surahFile, jsonObject.toString());
    }

    public boolean isSurahIndexExist() {
        final File indexFile = new File(this.destDir, QuranDiskSource.indexFileName);
        return indexFile.exists();
    }

    public boolean isSurahDetailAtNumberExist(int surahNumber) {
        final File indexFile = new File(this.destDir, surahNumber + ".json");
        return indexFile.exists();
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

    private String readStringFromFile(File source) {
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
}
