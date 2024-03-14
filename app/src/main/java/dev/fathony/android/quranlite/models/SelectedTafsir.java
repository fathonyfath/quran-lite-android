package dev.fathony.android.quranlite.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectedTafsir implements Parcelable {

    public static final Creator<SelectedTafsir> CREATOR = new Creator<SelectedTafsir>() {
        @Override
        public SelectedTafsir createFromParcel(Parcel in) {
            return new SelectedTafsir(in);
        }

        @Override
        public SelectedTafsir[] newArray(int size) {
            return new SelectedTafsir[size];
        }
    };

    private final Surah surah;
    private final int ayahNumber;
    private final String tafsir;
    private final String tafsirName;
    private final String tafsirSource;

    public SelectedTafsir(Surah surah, int ayahNumber, String tafsir, String tafsirName, String tafsirSource) {
        this.surah = surah;
        this.ayahNumber = ayahNumber;
        this.tafsir = tafsir;
        this.tafsirName = tafsirName;
        this.tafsirSource = tafsirSource;
    }

    /**
     * Parcelable implementation.
     */

    protected SelectedTafsir(Parcel in) {
        surah = in.readParcelable(Surah.class.getClassLoader());
        ayahNumber = in.readInt();
        tafsir = in.readString();
        tafsirName = in.readString();
        tafsirSource = in.readString();
    }

    public Surah getSurah() {
        return surah;
    }

    public int getAyahNumber() {
        return ayahNumber;
    }

    public String getTafsir() {
        return tafsir;
    }

    public String getTafsirName() {
        return tafsirName;
    }

    public String getTafsirSource() {
        return tafsirSource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(surah, flags);
        dest.writeInt(ayahNumber);
        dest.writeString(tafsir);
        dest.writeString(tafsirName);
        dest.writeString(tafsirSource);
    }
}
