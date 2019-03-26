package id.fathonyfath.quranreader.views.common;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import id.fathonyfath.quranreader.Res;
import id.fathonyfath.quranreader.utils.ViewBackStack;
import id.fathonyfath.quranreader.utils.ViewCallback;

public class BackstackNavigationView extends ContainerView {

    private final Map<Class, Integer> mappedClassToIndex;

    private ViewBackStack viewBackStack;
    private final ViewBackStack.Callback viewBackStackCallback = new ViewBackStack.Callback() {
        @Override
        public void onViewPushed(Class<? extends View> pushedView) {
            BackstackNavigationView.this.handleViewCallbackForPushedView(pushedView);

            BackstackNavigationView.this.updateViewBasedOnViewClass(pushedView);
        }

        @Override
        public void onViewPopped(Class<? extends View> poppedView) {
            Class<? extends View> topStackView = BackstackNavigationView.this.viewBackStack.peekView();

            if (topStackView != null) {
                BackstackNavigationView.this.updateViewBasedOnViewClass(topStackView);
            }

            BackstackNavigationView.this.handleViewCallbackForPoppedView(poppedView);
        }
    };


    public BackstackNavigationView(Context context) {
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
        final BackstackNavigationViewState viewState = new BackstackNavigationViewState(super.onSaveInstanceState());
        viewState.viewBackStack = this.viewBackStack;
        return viewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final BackstackNavigationViewState viewState = (BackstackNavigationViewState) state;
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

    private static class BackstackNavigationViewState extends BaseSavedState {

        private ViewBackStack viewBackStack;

        public BackstackNavigationViewState(Parcel source, ClassLoader loader) {
            super(source);

            this.viewBackStack = source.readParcelable(ViewBackStack.class.getClassLoader());
        }

        public BackstackNavigationViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeParcelable(this.viewBackStack, flags);
        }

        public static final Parcelable.Creator<BackstackNavigationViewState> CREATOR
                = new Parcelable.ClassLoaderCreator<BackstackNavigationViewState>() {
            @Override
            public BackstackNavigationViewState createFromParcel(Parcel in) {
                return new BackstackNavigationViewState(in, null);
            }

            @Override
            public BackstackNavigationViewState createFromParcel(Parcel in, ClassLoader loader) {
                return new BackstackNavigationViewState(in, loader);
            }

            @Override
            public BackstackNavigationViewState[] newArray(int size) {
                return new BackstackNavigationViewState[size];
            }
        };
    }
}
