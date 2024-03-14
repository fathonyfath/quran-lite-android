package dev.fathony.viewstack;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

public class DefaultBackstackHandler implements BackstackHandler {

    @NotNull
    private final Context context;
    @NotNull
    private final ViewGroup container;

    public DefaultBackstackHandler(@NotNull Context context, @NotNull ViewGroup container) {
        this.context = context;
        this.container = container;
    }

    @Override
    public void handleBackstackChange(@NotNull Navigator navigator,
                                      @NotNull Backstack oldStack,
                                      @NotNull Backstack newStack,
                                      @NotNull NavigationCommand command) {

        if (command == NavigationCommand.Replace || command == NavigationCommand.Restore) {
            this.container.removeAllViews();

            final ViewKey upcomingKey = newStack.peekKey();
            final ViewState upcomingViewState = newStack.obtainViewState(upcomingKey);
            final View view = buildView(upcomingKey);
            restoreViewState(view, upcomingViewState);

            container.addView(view);
            return;
        }

        if (oldStack.peekKey() != newStack.peekKey()) {
            final View topView = this.container.getChildAt(0);
            if (topView == null) {
                return;
            }

            if (command == NavigationCommand.Push) {
                saveViewState(topView, oldStack.obtainViewState(getViewKey(topView)));
                this.container.removeView(topView);

                final ViewKey upcomingViewKey = newStack.peekKey();
                final View viewToPush = buildView(upcomingViewKey);
                restoreViewState(viewToPush, newStack.obtainViewState(upcomingViewKey));
                this.container.addView(viewToPush);
            } else if (command == NavigationCommand.Pop) {
                this.container.removeView(topView);

                final ViewKey upcomingViewKey = newStack.peekKey();
                final View viewToPush = buildView(upcomingViewKey);
                restoreViewState(viewToPush, newStack.obtainViewState(upcomingViewKey));
                this.container.addView(viewToPush);
            }
        }
    }

    protected final void saveViewState(@NotNull View view, @NotNull ViewState viewState) {
        final SparseArray<Parcelable> hierarchyState = viewState.getHierarchyState();
        hierarchyState.clear();
        view.saveHierarchyState(hierarchyState);
    }

    protected final void restoreViewState(@NotNull View view, @NotNull ViewState viewState) {
        view.restoreHierarchyState(viewState.getHierarchyState());
    }

    protected final ViewKey getViewKey(@NotNull View view) {
        return ViewKeyContextWrapper.getViewKey(view.getContext());
    }

    protected final View buildView(@NotNull ViewKey viewKey) {
        final ViewKeyContextWrapper context = new ViewKeyContextWrapper(this.context, viewKey);
        return viewKey.buildView(context);
    }
}
