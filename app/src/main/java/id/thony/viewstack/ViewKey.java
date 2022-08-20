package id.thony.viewstack;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public abstract class ViewKey implements Parcelable {

    @NotNull
    public abstract View buildView(@NotNull Context context);
}
