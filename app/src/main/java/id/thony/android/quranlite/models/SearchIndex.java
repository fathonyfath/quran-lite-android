package id.thony.android.quranlite.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class SearchIndex implements Parcelable {

    public static final Creator<SearchIndex> CREATOR = new Creator<SearchIndex>() {
        @Override
        public SearchIndex createFromParcel(Parcel in) {
            return new SearchIndex(in);
        }

        @Override
        public SearchIndex[] newArray(int size) {
            return new SearchIndex[size];
        }
    };

    private final Surah surah;
    private final String[] indexes;

    public SearchIndex(Surah surah, String[] indexes) {
        this.surah = surah;
        this.indexes = indexes;
    }

    /**
     * Parcelable implementation.
     */
    protected SearchIndex(Parcel in) {
        surah = in.readParcelable(Surah.class.getClassLoader());
        indexes = in.createStringArray();
    }

    public Surah getSurah() {
        return surah;
    }

    public String[] getIndexes() {
        return indexes;
    }

    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchIndex that = (SearchIndex) o;

        if (surah != null ? !surah.equals(that.surah) : that.surah != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(indexes, that.indexes);
    }

    @Override
    public int hashCode() {
        int result = surah != null ? surah.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(indexes);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(surah, flags);
        dest.writeStringArray(indexes);
    }
}
