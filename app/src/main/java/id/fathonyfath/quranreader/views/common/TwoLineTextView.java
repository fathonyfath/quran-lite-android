package id.fathonyfath.quranreader.views.common;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TwoLineTextView extends LinearLayout {

    private final TextView firstLineTextView;
    private final TextView secondLineTextView;

    public TwoLineTextView(Context context) {
        super(context);
        this.firstLineTextView = new TextView(context);
        this.secondLineTextView = new TextView(context);

        initConfiguration();
        initView();
    }

    public void setTexts(String first, String second) {
        this.firstLineTextView.setText(first);
        this.secondLineTextView.setText(second);
    }

    private void initConfiguration() {
        setOrientation(LinearLayout.VERTICAL);
    }

    private void initView() {
        addView(this.firstLineTextView);
        addView(this.secondLineTextView);

        this.firstLineTextView.setTextSize(24f);
        this.secondLineTextView.setTextSize(16f);

        LinearLayout.LayoutParams firstParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams secondParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        this.firstLineTextView.setLayoutParams(firstParams);
        this.secondLineTextView.setLayoutParams(secondParams);
    }
}
