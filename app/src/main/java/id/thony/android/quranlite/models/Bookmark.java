package id.thony.android.quranlite.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Bookmark implements Parcelable {

    public static final Creator<Bookmark> CREATOR = new Creator<Bookmark>() {
        @Override
        public Bookmark createFromParcel(Parcel in) {
            return new Bookmark(in);
        }

        @Override
        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };

    private final int surahNumber;
    private final String surahName;
    private final String surahNameInLatin;
    private final int lastReadAyah;

    public Bookmark(int surahNumber, String surahName, String surahNameInLatin, int lastReadAyah) {
        this.surahNumber = surahNumber;
        this.surahName = surahName;
        this.surahNameInLatin = surahNameInLatin;
        this.lastReadAyah = lastReadAyah;
    }

    /**
     * Parcelable implementation.
     */

    protected Bookmark(Parcel in) {
        surahNumber = in.readInt();
        surahName = in.readString();
        surahNameInLatin = in.readString();
        lastReadAyah = in.readInt();
    }

    public int getSurahNumber() {
        return surahNumber;
    }

    public String getSurahName() {
        return surahName;
    }

    public String getSurahNameInLatin() {
        return surahNameInLatin;
    }

    public int getLastReadAyah() {
        return lastReadAyah;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(surahNumber);
        dest.writeString(surahName);
        dest.writeString(surahNameInLatin);
        dest.writeInt(lastReadAyah);
    }
}
