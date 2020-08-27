package id.thony.android.quranlite.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectedAyah implements Parcelable {

    public static final Creator<SelectedAyah> CREATOR = new Creator<SelectedAyah>() {
        @Override
        public SelectedAyah createFromParcel(Parcel in) {
            return new SelectedAyah(in);
        }

        @Override
        public SelectedAyah[] newArray(int size) {
            return new SelectedAyah[size];
        }
    };

    private final Surah surah;
    private final int ayahNumber;

    public SelectedAyah(Surah surah, int ayahNumber) {
        this.surah = surah;
        this.ayahNumber = ayahNumber;
    }

    /**
     * Parcelable implementation.
     */

    protected SelectedAyah(Parcel in) {
        surah = in.readParcelable(Surah.class.getClassLoader());
        ayahNumber = in.readInt();
    }

    public Surah getSurah() {
        return surah;
    }

    public int getAyahNumber() {
        return ayahNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(surah, flags);
        dest.writeInt(ayahNumber);
    }
}
