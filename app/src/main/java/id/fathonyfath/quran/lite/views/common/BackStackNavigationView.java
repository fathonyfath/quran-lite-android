package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import id.fathonyfath.quran.lite.Res;
import id.fathonyfath.quran.lite.utils.viewLifecycle.ViewBackStack;
import id.fathonyfath.quran.lite.utils.viewLifecycle.ViewCallback;

public abstract class BackStackNavigationView extends SwitchContainerView {

    private final Map<Class, Integer> mappedClassToIndex;

    private ViewBackStack viewBackStack;
    private final ViewBackStack.Callback viewBackStackCallback = new ViewBackStack.Callback() {
        @Override
        public void onViewPushed(Class<? extends View> pushedView) {
            BackStackNavigationView.this.handleViewCallbackForPushedView(pushedView);

            BackStackNavigationView.this.showViewBasedOnViewClass(pushedView);
        }

        @Override
        public void onViewPopped(Class<? extends View> poppedView) {
            Class<? extends View> topStackView = BackStackNavigationView.this.viewBackStack.peekView();

            if (topStackView != null) {
                BackStackNavigationView.this.showViewBasedOnViewClass(topStackView);
            }

            BackStackNavigationView.this.handleViewCallbackForPoppedView(poppedView);
        }
    };


    public BackStackNavigationView(Context context) {
        super(context);

        this.setId(Res.Id.navigationView);

        this.mappedClassToIndex = new HashMap<>();
        this.viewBackStack = new ViewBackStack();
        this.viewBackStack.setCallback(this.viewBackStackCallback);
    }

    protected abstract void initStack();

    protected void pushView(Class<? extends View> viewClass) {
        this.viewBackStack.pushView(viewClass);
    }

    protected boolean popView() {
        return this.viewBackStack.popView();
    }

    protected void registerView(Class viewClass, View view) {
        this.mappedClassToIndex.put(viewClass, addViewToContainer(view));
    }

    protected <T extends View> T findChildWithClass(Class viewClass) {
        return this.findChildViewAtIndex(this.mappedClassToIndex.get(viewClass));
    }

    public boolean onBackPressed() {
        if (this.viewBackStack.size() == 1) {
            this.viewBackStack.popView();
            return false;
        }
        return this.viewBackStack.popView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final BackStackNavigationViewState viewState = new BackStackNavigationViewState(super.onSaveInstanceState());
        viewState.viewBackStack = this.viewBackStack;

        return viewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final BackStackNavigationViewState viewState = (BackStackNavigationViewState) state;
        super.onRestoreInstanceState(viewState.getSuperState());
        this.viewBackStack = viewState.viewBackStack;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.viewBackStack.setCallback(this.viewBackStackCallback);
        if (this.viewBackStack.isEmpty()) {
            initStack();
        } else {
            showViewBasedOnViewClass(this.viewBackStack.peekView());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        this.viewBackStack.setCallback(null);

        super.onDetachedFromWindow();
    }

    private void showViewBasedOnViewClass(Class<? extends View> viewClass) {
        Integer indexOfView = this.mappedClassToIndex.get(viewClass);

        if (indexOfView != null) {
            this.showViewAtIndex(indexOfView);
        }
    }

    private void handleViewCallbackForPushedView(Class<? extends View> viewClass) {
        Integer indexOfView = this.mappedClassToIndex.get(viewClass);
        if (indexOfView != null) {
            View view = findChildViewAtIndex(indexOfView);
            if (view instanceof ViewCallback) {
                ViewCallback viewCallback = (ViewCallback) view;
                viewCallback.onStart();
            }
        }
    }

    private void handleViewCallbackForPoppedView(Class<? extends View> viewClass) {
        Integer indexOfView = this.mappedClassToIndex.get(viewClass);
        if (indexOfView != null) {
            View view = findChildViewAtIndex(indexOfView);
            if (view instanceof ViewCallback) {
                ViewCallback viewCallback = (ViewCallback) view;
                viewCallback.onStop();
            }
        }
    }

    private static class BackStackNavigationViewState extends BaseSavedState {

        public static final Parcelable.Creator<BackStackNavigationViewState> CREATOR
                = new Parcelable.ClassLoaderCreator<BackStackNavigationViewState>() {
            @Override
            public BackStackNavigationViewState createFromParcel(Parcel in) {
                return new BackStackNavigationViewState(in, null);
            }

            @Override
            public BackStackNavigationViewState createFromParcel(Parcel in, ClassLoader loader) {
                return new BackStackNavigationViewState(in, loader);
            }

            @Override
            public BackStackNavigationViewState[] newArray(int size) {
                return new BackStackNavigationViewState[size];
            }
        };
        private ViewBackStack viewBackStack;

        public BackStackNavigationViewState(Parcel source, ClassLoader loader) {
            super(source);

            this.viewBackStack = source.readParcelable(ViewBackStack.class.getClassLoader());
        }

        public BackStackNavigationViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeParcelable(this.viewBackStack, flags);
        }
    }
}
