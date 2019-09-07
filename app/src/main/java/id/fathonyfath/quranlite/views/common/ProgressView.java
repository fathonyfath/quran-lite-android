package id.fathonyfath.quranlite.views.common;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import id.fathonyfath.quranlite.Res;

public class ProgressView extends LinearLayout {

    private final ProgressBar progressBar;
    private final TextView progressText;

    public ProgressView(Context context) {
        super(context);
        setId(Res.Id.progressView);

        this.progressBar = new ProgressBar(context);
        this.progressText = new LpmqTextView(context);

        initConfiguration();
        initView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final ProgressViewState progressViewState = new ProgressViewState(super.onSaveInstanceState());
        progressViewState.currentText = this.progressText.getText().toString();
        return progressViewState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final ProgressViewState progressViewState = (ProgressViewState) state;
        this.progressText.setText(progressViewState.currentText);
        super.onRestoreInstanceState(progressViewState.getSuperState());
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

    public static class ProgressViewState extends BaseSavedState {

        private String currentText;

        public ProgressViewState(Parcel source, ClassLoader loader) {
            super(source);

            this.currentText = source.readString();
        }

        public ProgressViewState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeString(this.currentText);
        }

        public static final Parcelable.Creator<ProgressViewState> CREATOR
                = new Parcelable.ClassLoaderCreator<ProgressViewState>() {
            @Override
            public ProgressViewState createFromParcel(Parcel in) {
                return new ProgressViewState(in, null);
            }

            @Override
            public ProgressViewState createFromParcel(Parcel in, ClassLoader loader) {
                return new ProgressViewState(in, loader);
            }

            @Override
            public ProgressViewState[] newArray(int size) {
                return new ProgressViewState[size];
            }
        };
    }
}
