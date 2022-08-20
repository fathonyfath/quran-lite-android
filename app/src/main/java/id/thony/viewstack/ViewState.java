package id.thony.viewstack;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import org.jetbrains.annotations.NotNull;

public final class ViewState implements Parcelable {
    public static final Creator<ViewState> CREATOR = new Creator<ViewState>() {
        @NotNull
        @Override
        public ViewState createFromParcel(@NotNull Parcel in) {
            return new ViewState(in);
        }

        @NotNull
        @Override
        public ViewState[] newArray(int size) {
            return new ViewState[size];
        }
    };

    @NotNull
    private final SparseArray<Parcelable> hierarchyState;

    protected ViewState() {
        this.hierarchyState = new SparseArray<>();
    }

    protected ViewState(@NotNull Parcel in) {
        this.hierarchyState = in.readSparseArray(getClass().getClassLoader());
    }

    @NotNull
    public SparseArray<Parcelable> getHierarchyState() {
        return hierarchyState;
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeSparseArray(this.hierarchyState);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
