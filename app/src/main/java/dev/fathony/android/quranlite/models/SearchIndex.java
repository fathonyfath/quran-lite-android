package dev.fathony.android.quranlite.models;

import android.annotation.SuppressLint;
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
    private final String[] indices;

    public SearchIndex(Surah surah, String[] indices) {
        this.surah = surah;
        this.indices = indices;
    }

    /**
     * Parcelable implementation.
     */
    protected SearchIndex(Parcel in) {
        surah = in.readParcelable(Surah.class.getClassLoader());
        indices = in.createStringArray();
    }

    public Surah getSurah() {
        return surah;
    }

    public String[] getIndices() {
        return indices;
    }

    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchIndex that = (SearchIndex) o;

        if (surah != null ? !surah.equals(that.surah) : that.surah != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(indices, that.indices);
    }

    @Override
    public int hashCode() {
        int result = surah != null ? surah.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(indices);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(surah, flags);
        dest.writeStringArray(indices);
    }
}
