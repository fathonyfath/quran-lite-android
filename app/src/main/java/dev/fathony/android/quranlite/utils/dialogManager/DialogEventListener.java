package dev.fathony.android.quranlite.utils.dialogManager;

import android.os.Parcelable;

public interface DialogEventListener {
    void onEvent(DialogEvent event, Parcelable arguments);
}
