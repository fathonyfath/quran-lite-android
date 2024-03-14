package dev.fathony.android.quranlite;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import dev.fathony.android.quranlite.utils.dialogManager.DialogEventListener;
import dev.fathony.android.quranlite.utils.dialogManager.DialogEventListeners;

public class MainActivity extends Activity implements DialogEventListeners {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void relaunchActivity() {
        getWindow().setWindowAnimations(R.style.WindowAnimation);
        recreate();
    }

    @Override
    public List<DialogEventListener> getDialogEventListeners() {
        return new ArrayList<>();
    }
}
