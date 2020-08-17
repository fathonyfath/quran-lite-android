package id.fathonyfath.quran.lite.utils.dialogManager;

import android.app.Dialog;
import android.content.Context;
import android.os.Parcelable;
import android.view.ViewGroup;
import android.view.Window;

public abstract class SimpleDialog extends Dialog {

    private Parcelable arguments;

    public SimpleDialog(Context context, Parcelable arguments) {
        super(context);

        this.arguments = arguments;

        if (getWindow() != null) {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    protected ViewGroup.LayoutParams getLayoutParams() {
        return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @SuppressWarnings("unchecked")
    protected final <T extends Parcelable> T getSafeCastArguments() {
        try {
            return (T) arguments;
        } catch (ClassCastException ignored) {
            return null;
        }
    }

    @Override
    public final void show() {
        super.show();

        if (getWindow() != null) {
            getWindow().setLayout(getLayoutParams().width, getLayoutParams().height);
        }
    }

    public interface Factory {
        SimpleDialog create(Context context, Parcelable parcelable);
    }
}