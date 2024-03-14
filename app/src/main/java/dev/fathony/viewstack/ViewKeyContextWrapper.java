package dev.fathony.viewstack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewKeyContextWrapper extends ContextWrapper {
    private static final String ViewKey = "ViewKey";
    @NotNull
    private final ViewKey viewKey;
    @Nullable
    private LayoutInflater layoutInflater;

    public ViewKeyContextWrapper(@NotNull Context base, @NotNull ViewKey viewKey) {
        super(base);
        this.viewKey = viewKey;
    }

    @SuppressLint("WrongConstant")
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T extends ViewKey> T getViewKey(@NotNull Context context) {
        return (T) context.getSystemService(ViewKey);
    }

    @Override
    public Object getSystemService(String name) {
        if (name.equals(Context.LAYOUT_INFLATER_SERVICE)) {
            if (this.layoutInflater == null) {
                this.layoutInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return this.layoutInflater;
        }
        if (name.equals(ViewKey)) {
            return viewKey;
        }
        return super.getSystemService(name);
    }
}
