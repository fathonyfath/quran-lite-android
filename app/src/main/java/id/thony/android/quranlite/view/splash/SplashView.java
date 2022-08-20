package id.thony.android.quranlite.view.splash;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public final class SplashView extends FrameLayout {

    public SplashView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        TextView text = new TextView(getContext());
        text.setText("Hello world!");
        addView(text, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
