package id.fathonyfath.quranlite.data.remote;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FontService {

    private final String BASE_URL = "https://fathonyfath.github.io/external-resource/fonts/";

    private OnDownloadProgressListener onDownloadProgressListener;

    public boolean downloadFont(File destination) {
        return doGetRequestAndSaveFile(BASE_URL + "lpmq.otf", destination);
    }

    public void setOnDownloadProgressListener(OnDownloadProgressListener onDownloadProgressListener) {
        this.onDownloadProgressListener = onDownloadProgressListener;
    }

    private boolean doGetRequestAndSaveFile(String url, File destination) {
        HttpURLConnection urlConnection = null;
        try {
            URL indexUrl = new URL(url);
            urlConnection = (HttpURLConnection) indexUrl.openConnection();
            urlConnection.setRequestProperty("Accept-Encoding", "identity");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            int contentLength = urlConnection.getContentLength();
            int totalRead = 0;

            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
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

            FileOutputStream fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(outputStream.toByteArray());

            stream.close();
            outputStream.close();
            fileOutputStream.close();

            return true;
        } catch (MalformedURLException ignored) {

        } catch (IOException ignored) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return false;
    }
}
