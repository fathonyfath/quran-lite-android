package dev.fathony.android.quranlite.data.font;

import java.io.File;

import dev.fathony.android.quranlite.utils.network.NetworkHelper;

public class FontRemoteSource {
    private final String BASE_URL = "https://fathonyfath.github.io/external-resource/fonts/";

    public boolean downloadFont(File destination,
                                NetworkHelper.CancelSignal cancelSignal,
                                NetworkHelper.ProgressListener progressListener) {
        return NetworkHelper.doGetRequestAndSaveToFile(
                BASE_URL + "lpmq.otf",
                destination,
                cancelSignal,
                progressListener);
    }
}
