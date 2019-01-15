package id.fathonyfath.quranreader.views.common;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressView extends LinearLayout {

    private final ProgressBar progressBar;
    private final TextView progressText;

    public ProgressView(Context context) {
        super(context);

        this.progressBar = new ProgressBar(context);
        this.progressText = new TextView(context);

        initConfiguration();
        initView();
    }

    public void updateProgress(float progress) {
        this.progressText.setText(String.valueOf(Math.round(progress)) + "%");
    }

    private void initConfiguration() {
        setOrientation(LinearLayout.VERTICAL);
    }

    private void initView() {
        LinearLayout.LayoutParams firstParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams secondParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        firstParams.gravity = Gravity.CENTER_HORIZONTAL;
        secondParams.gravity = Gravity.CENTER_HORIZONTAL;

        addView(this.progressBar, firstParams);
        addView(this.progressText, secondParams);

        this.progressBar.setIndeterminate(true);
    }
}
