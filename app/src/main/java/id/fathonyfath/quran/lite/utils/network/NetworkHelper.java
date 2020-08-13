package id.fathonyfath.quran.lite.utils.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

@SuppressWarnings("CharsetObjectCanBeUsed")
public class NetworkHelper {

    private NetworkHelper() {
    }

    public static String doGetRequest(String url, CancelSignal signal, ProgressListener listener) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            if (!doGetRequest(url, outputStream, signal, listener)) {
                throw new IOException();
            }

            final StringBuilder sb = new StringBuilder();

            byte[] contentByteArray = outputStream.toByteArray();
            sb.append(new String(contentByteArray, 0, contentByteArray.length, Charset.forName("UTF-8")));

            outputStream.close();

            cancelSignalCheck(signal);

            return sb.toString();
        } catch (IOException ignored) {

        }

        return null;
    }

    public static boolean doGetRequestAndSaveToFile(String url, File destination, CancelSignal signal, ProgressListener listener) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            if (!doGetRequest(url, outputStream, signal, listener)) {
                throw new IOException();
            }

            cancelSignalCheck(signal);

            FileOutputStream fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(outputStream.toByteArray());

            outputStream.close();
            fileOutputStream.close();

            return true;
        } catch (IOException ignored) {

        }

        return false;
    }

    private static boolean doGetRequest(String url, OutputStream into, CancelSignal signal, ProgressListener listener) {
        HttpURLConnection urlConnection = null;

        try {
            URL indexUrl = new URL(url);
            urlConnection = (HttpURLConnection) indexUrl.openConnection();
            urlConnection.setRequestProperty("Accept-Encoding", "identity");
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(2000);

            urlConnection.connect();

            cancelSignalCheck(signal);

            int contentLength = urlConnection.getContentLength();
            int totalRead = 0;

            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = stream.read(buffer)) != -1) {
                cancelSignalCheck(signal);

                into.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                if (listener != null) {
                    listener.onProgress(totalRead, contentLength);
                }
            }

            stream.close();

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

    private static void cancelSignalCheck(CancelSignal signal) throws IOException {
        if (signal != null && signal.isCancelled()) throw new IOException();
    }

    public interface CancelSignal {
        boolean isCancelled();
    }

    public interface ProgressListener {
        void onProgress(int currentReadByte, int maxReadByte);
    }
}
