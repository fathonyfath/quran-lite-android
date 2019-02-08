package id.fathonyfath.quranreader.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.Stack;

public class ViewBackStack implements Parcelable {

    private Stack<Class<? extends View>> viewBackStack;
    private ViewBackStack.Callback callback;

    public ViewBackStack() {
        this.viewBackStack = new Stack<>();
    }

    public void pushView(Class<? extends View> viewClass) {
        this.viewBackStack.push(viewClass);

        if (this.callback != null) {
            this.callback.onViewPushed(viewClass);
        }
    }

    public boolean popView() {
        if (this.viewBackStack.isEmpty()) {
            return false;
        }

        Class<? extends View> viewToBePopped = this.viewBackStack.pop();

        if (this.callback != null) {
            this.callback.onViewPopped(viewToBePopped);
        }

        return true;
    }

    public Class<? extends View> peekView() {
        if (this.viewBackStack.isEmpty()) {
            return null;
        }

        return this.viewBackStack.peek();
    }

    public boolean isEmpty() {
        return this.viewBackStack.isEmpty();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Parcelable implementation.
     */

    protected ViewBackStack(Parcel in) {
        int size = in.readInt();
        Class[] classArray = (Class[]) in.readSerializable();

        for (int i = 0; i < size; i++) {
            this.viewBackStack.add(i, classArray[i]);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.viewBackStack.size());

        Class[] classArray = this.viewBackStack.toArray(new Class[0]);
        dest.writeSerializable(classArray);
    }

    public static final Creator<ViewBackStack> CREATOR = new Creator<ViewBackStack>() {
        @Override
        public ViewBackStack createFromParcel(Parcel in) {
            return new ViewBackStack(in);
        }

        @Override
        public ViewBackStack[] newArray(int size) {
            return new ViewBackStack[size];
        }
    };

    public interface Callback {

        void onViewPushed(Class<? extends View> pushedView);

        void onViewPopped(Class<? extends View> poppedView);
    }
}
