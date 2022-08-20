package id.thony.viewstack;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.jetbrains.annotations.NotNull;

import java.util.EmptyStackException;
import java.util.Stack;

public final class Backstack implements Parcelable {

    public static final Creator<Backstack> CREATOR = new Creator<Backstack>() {
        @NotNull
        @Override
        public Backstack createFromParcel(@NotNull Parcel in) {
            return new Backstack(in);
        }

        @NotNull
        @Override
        public Backstack[] newArray(int size) {
            return new Backstack[size];
        }
    };
    @NotNull
    private final Stack<Pair<ViewKey, ViewState>> history;

    private Backstack(@NotNull Stack<Pair<ViewKey, ViewState>> history) {
        if (history.size() <= 0) {
            throw new IllegalStateException();
        }

        this.history = history;
    }

    /*
     * Parcelable implementation.
     */
    protected Backstack(@NotNull Parcel in) {
        int size = in.readInt();
        Parcelable[] viewKeys = in.readParcelableArray(getClass().getClassLoader());
        Parcelable[] viewStates = in.readParcelableArray(getClass().getClassLoader());

        this.history = new Stack<>();

        for (int i = 0; i < size; i++) {
            final ViewKey viewKey = (ViewKey) viewKeys[i];
            final ViewState viewState = (ViewState) viewStates[i];
            this.history.add(i, Pair.create(viewKey, viewState));
        }
    }

    @NotNull
    public static Backstack of(@NotNull ViewKey... keys) {
        final Stack<Pair<ViewKey, ViewState>> viewStack = new Stack<>();
        for (ViewKey key : keys) {
            viewStack.push(Pair.create(key, new ViewState()));
        }
        return new Backstack(viewStack);
    }

    protected void pushKey(@NotNull ViewKey viewKey) {
        this.history.push(Pair.create(viewKey, new ViewState()));
    }

    protected boolean popKey() {
        if (this.history.size() == 1) {
            return false;
        }

        try {
            this.history.pop();
            return true;
        } catch (EmptyStackException ignored) {
            return false;
        }
    }

    @NotNull
    public ViewKey peekKey() {
        return this.history.peek().first;
    }

    public int count() {
        return this.history.size();
    }

    protected void clearHistory() {
        this.history.clear();
    }

    @NotNull
    public ViewState obtainViewState(@NotNull ViewKey viewKey) {
        ViewState foundViewState = null;
        for (Pair<ViewKey, ViewState> pair : this.history) {
            if (pair.first == viewKey) {
                foundViewState = pair.second;
                break;
            }
        }

        if (foundViewState != null) {
            return foundViewState;
        } else {
            throw new IllegalStateException("Cannot find ViewState for specified ViewKey. " +
                    "Are you sure you get the ViewState from the Backstack object?");
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @NotNull
    @Override
    protected Backstack clone() {
        //noinspection unchecked
        final Stack<Pair<ViewKey, ViewState>> viewKeyStack =
                (Stack<Pair<ViewKey, ViewState>>) this.history.clone();
        return new Backstack(viewKeyStack);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        final int size = this.history.size();
        dest.writeInt(this.history.size());

        Parcelable[] viewKeys = new Parcelable[size];
        Parcelable[] viewStates = new Parcelable[size];
        for (int i = 0; i < size; i++) {
            Pair<ViewKey, ViewState> pairKeyValue = this.history.get(i);
            viewKeys[i] = pairKeyValue.first;
            viewStates[i] = pairKeyValue.second;
        }
        dest.writeParcelableArray(viewKeys, flags);
        dest.writeParcelableArray(viewStates, flags);
    }

    @NotNull
    @Override
    public String toString() {
        final String prepend = super.toString();
        return "ViewBackstack(" + prepend + "){stack=" + history + "}";
    }
}
