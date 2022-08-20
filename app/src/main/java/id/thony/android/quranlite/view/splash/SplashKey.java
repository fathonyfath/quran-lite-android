package id.thony.android.quranlite.view.splash;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import id.thony.viewstack.ViewKey;

public class SplashKey extends ViewKey implements Parcelable {

    public static final Creator<SplashKey> CREATOR = new Creator<SplashKey>() {
        @Override
        public SplashKey createFromParcel(Parcel in) {
            return new SplashKey(in);
        }

        @Override
        public SplashKey[] newArray(int size) {
            return new SplashKey[size];
        }
    };

    public SplashKey() {
    }

    @Override
    public @NotNull View buildView(@NotNull Context context) {
        return new SplashView(context);
    }

    /**
     * Parcelable implementation
     */
    protected SplashKey(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
