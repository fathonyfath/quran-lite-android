package id.thony.android.quranlite.view.fontDownloader;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import id.thony.viewstack.ViewKey;

public class FontDownloaderKey extends ViewKey implements Parcelable {

    public static final Creator<FontDownloaderKey> CREATOR = new Creator<FontDownloaderKey>() {
        @Override
        public FontDownloaderKey createFromParcel(Parcel in) {
            return new FontDownloaderKey(in);
        }

        @Override
        public FontDownloaderKey[] newArray(int size) {
            return new FontDownloaderKey[size];
        }
    };

    public FontDownloaderKey() {
    }

    @Override
    public @NotNull View buildView(@NotNull Context context) {
        return new FontDownloaderView(context);
    }

    protected FontDownloaderKey(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
