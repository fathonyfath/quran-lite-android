package id.fathonyfath.quranlite.views.common;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import id.fathonyfath.quranlite.Res;
import id.fathonyfath.quranlite.utils.ViewBackStack;
import id.fathonyfath.quranlite.utils.ViewCallback;

public class BackStackNavigationView extends ContainerView {

    private final Map<Class, Integer> mappedClassToIndex;

    private ViewBackStack viewBackStack;
    private final ViewBackStack.Callback viewBackStackCallback = new ViewBackStack.Callback() {
        @Override
        public void onViewPushed(Class<? extends View> pushedView) {
            BackStackNavigationView.this.handleViewCallbackForPushedView(pushedView);

            BackStackNavigationView.this.updateViewBasedOnViewClass(pushedView);
        }

        @Override
        public void onViewPopped(Class<? extends View> poppedView) {
            Class<? extends View> topStackView = BackStackNavigationView.this.viewBackStack.peekView();

            if (topStackView != null) {
                BackStackNavigationView.this.updateViewBasedOnViewClass(topStackView);
            }

            BackStackNavigationView.this.handleViewCallbackForPoppedView(poppedView);
        }
    };


    public BackStackNavigationView(Context context) {
        super(context);

        this.setId(Res.Id.navigationView);

        this.mappedClassToIndex = new HashMap<>();
        this.viewBackStack = new ViewBackStack();

    }

    public boolean onBackPressed() {
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
        this.viewBackStack.setCallback(this.viewBackStackCallback);
    }

    private void updateViewBasedOnViewClass(Class<? extends View> viewClass) {
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
    }
}
