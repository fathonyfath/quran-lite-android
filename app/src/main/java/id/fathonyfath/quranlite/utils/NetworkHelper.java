package id.fathonyfath.quranlite.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkHelper {

    public static String doGetRequest(String url, CancelSignal signal, ProgressListener listener) {
        HttpURLConnection urlConnection = null;
        try {
            URL indexUrl = new URL(url);
            urlConnection = (HttpURLConnection) indexUrl.openConnection();
            urlConnection.setRequestProperty("Accept-Encoding", "identity");
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            cancelSignalCheck(signal);

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
                cancelSignalCheck(signal);

                outputStream.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                if (listener != null) {
                    listener.onProgress(totalRead, contentLength);
                }
            }

            stream.close();

            byte[] contentByteArray = outputStream.toByteArray();
            sb.append(new String(contentByteArray, 0, contentByteArray.length, charset.toUpperCase()));

            cancelSignalCheck(signal);

            return sb.toString();
        } catch (MalformedURLException ignored) {

        } catch (IOException ignored) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
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
