package dev.fathony.android.quranlite.data.source.disk;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class SearchIndexDiskSource {

    private static final String searchIndexFileName = "search_index.json";
    private final File destDir;

    public SearchIndexDiskSource(Context context) {
        this.destDir = new File(context.getFilesDir(), "contents");
        this.destDir.mkdirs();
    }

    public JSONArray getSearchIndices() {
        final File indexFile = new File(this.destDir, searchIndexFileName);
        if (!indexFile.exists()) {
            return null;
        }

        try {
            final String jsonFile = readStringFromFile(indexFile);

            if (jsonFile == null) {
                return null;
            }

            return new JSONArray(jsonFile);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public boolean isSearchIndicesExist() {
        final File indexFile = new File(this.destDir, searchIndexFileName);
        return indexFile.exists();
    }

    public boolean saveSearchIndices(JSONArray jsonArray) {
        final File indexFile = new File(this.destDir, searchIndexFileName);
        if (indexFile.exists()) {
            indexFile.delete();
        }

        return saveStringToFile(indexFile, jsonArray.toString());
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
}
