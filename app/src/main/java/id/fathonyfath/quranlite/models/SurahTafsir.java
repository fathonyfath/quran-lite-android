package id.fathonyfath.quranlite.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;
import java.util.TreeMap;

public class SurahTafsir implements Parcelable {

    public static final Creator<SurahTafsir> CREATOR = new Creator<SurahTafsir>() {
        @Override
        public SurahTafsir createFromParcel(Parcel in) {
            return new SurahTafsir(in);
        }

        @Override
        public SurahTafsir[] newArray(int size) {
            return new SurahTafsir[size];
        }
    };
    private final String name;
    private final String source;
    private final Map<Integer, String> contents = new TreeMap<>();

    public SurahTafsir(String name, String source, Map<Integer, String> contents) {
        this.name = name;
        this.source = source;

        for (Map.Entry<Integer, String> entry : contents.entrySet()) {
            this.contents.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Parcelable implementation.
     */

    protected SurahTafsir(Parcel in) {
        this.name = in.readString();
        this.source = in.readString();

        int length = in.readInt();
        int[] keySet = new int[length];
        in.readIntArray(keySet);

        String[] valueSet = new String[length];
        in.readStringArray(valueSet);

        for (int i = 0; i < length; i++) {
            this.contents.put(keySet[i], valueSet[i]);
        }
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public Map<Integer, String> getContents() {
        return contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SurahTafsir that = (SurahTafsir) o;

        if (!name.equals(that.name)) return false;
        if (!source.equals(that.source)) return false;
        return contents.equals(that.contents);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + contents.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.source);

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
