package id.fathonyfath.quranlite.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;
import java.util.TreeMap;

public class SurahDetail implements Parcelable {

    private final int number;
    private final String name;
    private final String nameInLatin;
    private final int numberOfAyah;
    private final Map<Integer, String> contents = new TreeMap<>();
    private final SurahTranslation surahTranslation;
    private final SurahTafsir surahTafsir;

    public SurahDetail(int number,
                       String name,
                       String nameInLatin,
                       int numberOfAyah,
                       Map<Integer, String> contents,
                       SurahTranslation surahTranslation,
                       SurahTafsir surahTafsir) {
        this.number = number;
        this.name = name;
        this.nameInLatin = nameInLatin;
        this.numberOfAyah = numberOfAyah;
        this.surahTranslation = surahTranslation;
        this.surahTafsir = surahTafsir;

        for (Map.Entry<Integer, String> entry : contents.entrySet()) {
            this.contents.put(entry.getKey(), entry.getValue());
        }
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

    public Map<Integer, String> getContents() {
        return contents;
    }

    public SurahTranslation getSurahTranslation() {
        return surahTranslation;
    }

    public SurahTafsir getSurahTafsir() {
        return surahTafsir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SurahDetail that = (SurahDetail) o;

        if (number != that.number) return false;
        if (numberOfAyah != that.numberOfAyah) return false;
        if (!name.equals(that.name)) return false;
        if (!nameInLatin.equals(that.nameInLatin)) return false;
        if (!contents.equals(that.contents)) return false;
        if (!surahTranslation.equals(that.surahTranslation)) return false;
        return surahTafsir.equals(that.surahTafsir);
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + name.hashCode();
        result = 31 * result + nameInLatin.hashCode();
        result = 31 * result + numberOfAyah;
        result = 31 * result + contents.hashCode();
        result = 31 * result + surahTranslation.hashCode();
        result = 31 * result + surahTafsir.hashCode();
        return result;
    }

    /**
     * Parcelable implementation.
     */

    protected SurahDetail(Parcel in) {
        this.number = in.readInt();
        this.name = in.readString();
        this.nameInLatin = in.readString();
        this.numberOfAyah = in.readInt();
        this.surahTranslation = in.readParcelable(SurahTranslation.class.getClassLoader());
        this.surahTafsir = in.readParcelable(SurahTafsir.class.getClassLoader());

        int length = in.readInt();
        int[] keySet = new int[length];
        in.readIntArray(keySet);

        String[] valueSet = new String[length];
        in.readStringArray(valueSet);

        for (int i = 0; i < length; i++) {
            this.contents.put(keySet[i], valueSet[i]);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.number);
        dest.writeString(this.name);
        dest.writeString(this.nameInLatin);
        dest.writeInt(this.numberOfAyah);
        dest.writeParcelable(this.surahTranslation, flags);
        dest.writeParcelable(this.surahTafsir, flags);

        int[] keySet = new int[this.contents.size()];
        int currentIndex = 0;
        for (Integer key : this.contents.keySet()) {
            keySet[currentIndex++] = key;
        }

        String[] valueSet = this.contents.values().toArray(new String[0]);

        dest.writeIntArray(keySet);
        dest.writeStringArray(valueSet);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SurahDetail> CREATOR = new Creator<SurahDetail>() {
        @Override
        public SurahDetail createFromParcel(Parcel in) {
            return new SurahDetail(in);
        }

        @Override
        public SurahDetail[] newArray(int size) {
            return new SurahDetail[size];
        }
    };
}
