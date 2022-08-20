package id.thony.viewstack;

import org.jetbrains.annotations.NotNull;

public interface BackstackHandler {

    void handleBackstackChange(@NotNull Navigator navigator,
                               @NotNull Backstack oldStack,
                               @NotNull Backstack newStack,
                               @NotNull NavigationCommand command);
}
