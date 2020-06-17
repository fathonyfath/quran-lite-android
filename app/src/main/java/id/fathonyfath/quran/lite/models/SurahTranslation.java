package id.fathonyfath.quran.lite.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;
import java.util.TreeMap;

public class SurahTranslation implements Parcelable {

    public static final Creator<SurahTranslation> CREATOR = new Creator<SurahTranslation>() {
        @Override
        public SurahTranslation createFromParcel(Parcel in) {
            return new SurahTranslation(in);
        }

        @Override
        public SurahTranslation[] newArray(int size) {
            return new SurahTranslation[size];
        }
    };
    private final String surahName;
    private final Map<Integer, String> contents = new TreeMap<>();

    public SurahTranslation(String surahName, Map<Integer, String> contents) {
        this.surahName = surahName;

        for (Map.Entry<Integer, String> entry : contents.entrySet()) {
            this.contents.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Parcelable implementation.
     */

    protected SurahTranslation(Parcel in) {
        this.surahName = in.readString();

        int length = in.readInt();
        int[] keySet = new int[length];
        in.readIntArray(keySet);

        String[] valueSet = new String[length];
        in.readStringArray(valueSet);

        for (int i = 0; i < length; i++) {
            this.contents.put(keySet[i], valueSet[i]);
        }
    }

    public String getSurahName() {
        return surahName;
    }

    public Map<Integer, String> getContents() {
        return contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SurahTranslation that = (SurahTranslation) o;

        if (!surahName.equals(that.surahName)) return false;
        return contents.equals(that.contents);
    }

    @Override
    public int hashCode() {
        int result = surahName.hashCode();
        result = 31 * result + contents.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.surahName);

        dest.writeInt(this.contents.size());

        int[] keySet = new int[this.contents.size()];
        int currentIndex = 0;
        for (Integer key : this.contents.keySet()) {
            keySet[currentIndex++] = key;
        }

        String[] valueSet = this.contents.values().toArray(new String[0]);

        dest.writeIntArray(keySet);
        dest.writeStringArray(valueSet);
    }
}
