package id.fathonyfath.quran.lite.utils.dialogManager;

import android.content.Context;
import android.os.Parcelable;
import android.view.ViewGroup;
import android.view.Window;

public abstract class Dialog extends android.app.Dialog {

    private Parcelable arguments;
    private DialogEventListener listener;

    public Dialog(Context context, Parcelable arguments, DialogEventListener listener) {
        super(context);

        this.arguments = arguments;
        this.listener = listener;

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

    protected void sendDialogEvent(DialogEvent event, Parcelable arguments) {
        this.listener.onEvent(event, arguments);
    }

    @SuppressWarnings("unchecked")
    protected final <T extends Parcelable> T getSafeCastArguments() {
        if (arguments != null) {
            try {
                return (T) arguments;
            } catch (ClassCastException ignored) {
                return null;
            }
        } else {
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
        Dialog create(Context context, Parcelable parcelable, DialogEventListener listener);
    }
}