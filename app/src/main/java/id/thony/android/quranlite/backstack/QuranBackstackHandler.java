package id.thony.android.quranlite.backstack;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.viewstack.Backstack;
import id.thony.viewstack.BackstackHandler;
import id.thony.viewstack.NavigationCommand;
import id.thony.viewstack.Navigator;
import id.thony.viewstack.ViewKey;
import id.thony.viewstack.ViewKeyContextWrapper;
import id.thony.viewstack.ViewState;

public final class QuranBackstackHandler implements BackstackHandler {

    @NotNull
    private final Context context;
    @NotNull
    private final ViewGroup container;
    @Nullable
    private ThemeContext themeContext;

    public QuranBackstackHandler(@NotNull Context context, @NotNull ViewGroup container) {
        this.context = context;
        this.container = container;
    }

    public void updateTheme(@NotNull BaseTheme theme) {
        this.themeContext = new ThemeContext(this.context, theme);
    }

    @Override
    public void handleBackstackChange(@NotNull Navigator navigator, @NotNull Backstack oldStack, @NotNull Backstack newStack, @NotNull NavigationCommand command) {
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

    private void saveViewState(@NotNull View view, @NotNull ViewState viewState) {
        final SparseArray<Parcelable> hierarchyState = viewState.getHierarchyState();
        hierarchyState.clear();
        view.saveHierarchyState(hierarchyState);
    }

    private void restoreViewState(@NotNull View view, @NotNull ViewState viewState) {
        view.restoreHierarchyState(viewState.getHierarchyState());
    }

    private ViewKey getViewKey(@NotNull View view) {
        return ViewKeyContextWrapper.getViewKey(view.getContext());
    }

    private View buildView(@NotNull ViewKey viewKey) {
        final Context base = (this.themeContext != null) ? this.themeContext : this.context;
        final ViewKeyContextWrapper context = new ViewKeyContextWrapper(base, viewKey);
        return viewKey.buildView(context);
    }
}
