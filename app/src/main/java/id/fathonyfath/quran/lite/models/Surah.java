package id.fathonyfath.quran.lite.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Surah implements Parcelable {

    public static final Creator<Surah> CREATOR = new Creator<Surah>() {
        @Override
        public Surah createFromParcel(Parcel in) {
            return new Surah(in);
        }

        @Override
        public Surah[] newArray(int size) {
            return new Surah[size];
        }
    };
    private final int number;
    private final String name;
    private final String nameInLatin;
    private final int numberOfAyah;

    public Surah(int number, String name, String nameInLatin, int numberOfAyah) {
        this.number = number;
        this.name = name;
        this.nameInLatin = nameInLatin;
        this.numberOfAyah = numberOfAyah;
    }

    /**
     * Parcelable implementation.
     */

    protected Surah(Parcel in) {
        this.number = in.readInt();
        this.name = in.readString();
        this.nameInLatin = in.readString();
        this.numberOfAyah = in.readInt();
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getNameInLatin() {
        return nameInLatin;
    }

    public int getNumberOfAyah() {
        return numberOfAyah;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Surah surah = (Surah) o;

        if (number != surah.number) return false;
        if (numberOfAyah != surah.numberOfAyah) return false;
        if (!name.equals(surah.name)) return false;
        return nameInLatin.equals(surah.nameInLatin);
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + name.hashCode();
        result = 31 * result + nameInLatin.hashCode();
        result = 31 * result + numberOfAyah;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.number);
        dest.writeString(this.name);
        dest.writeString(this.nameInLatin);
        dest.writeInt(this.numberOfAyah);
    }

}
