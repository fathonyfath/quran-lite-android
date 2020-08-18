package id.fathonyfath.quran.lite.utils.dialogManager;

import android.os.Parcelable;

public interface DialogEventListener {
    void onEvent(DialogEvent event, Parcelable arguments);
}