package id.thony.android.quranlite.views.requestNotificationPermissionDialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.UnitConverter;
import id.thony.android.quranlite.utils.dialogManager.Dialog;
import id.thony.android.quranlite.utils.dialogManager.DialogEvent;
import id.thony.android.quranlite.utils.dialogManager.DialogEventListener;
import id.thony.android.quranlite.views.common.ButtonView;
import id.thony.android.quranlite.views.common.LpmqTextView;

public class ExplainNotificationPermissionDialog extends Dialog {

    private final LinearLayout container;

    private final LpmqTextView titleText;
    private final View separator;
    private final LpmqTextView descriptionText;
    private final ButtonView confirmation;

    private final View.OnClickListener onConfirmationClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ExplainNotificationPermissionDialog.this.sendDialogEvent(new ProceedPermissionRequest(), new EmptyParcel());
            ExplainNotificationPermissionDialog.this.dismiss();
        }
    };
    
    public ExplainNotificationPermissionDialog(Context context, Parcelable arguments, DialogEventListener listener) {
        super(context, arguments, listener);

        this.container = new LinearLayout(context);

        this.titleText = new LpmqTextView(context);
        this.separator = new View(context);
        this.descriptionText = new LpmqTextView(context);
        this.confirmation = new ButtonView(context);

        initDialog();
        applyStyleBasedOnTheme();
    }

    private void initDialog() {
        this.container.setOrientation(LinearLayout.VERTICAL);

        this.titleText.setText("Izin notifikasi dibutuhkan");
        this.descriptionText.setText("Untuk bisa mengunduh surah pada background, dibutuhkan izin untuk menampilkan notifikasi.");

        this.titleText.setTextSize(20.0f);
        this.titleText.setGravity(Gravity.CENTER_VERTICAL);
        this.titleText.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0
        );

        this.confirmation.setText("Mengerti");
        this.confirmation.setOnClickListener(this.onConfirmationClickListener);

        this.descriptionText.setTextSize(16.0f);
        this.descriptionText.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0
        );

        this.container.addView(this.titleText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        this.container.addView(this.separator, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) UnitConverter.fromDpToPx(getContext(), 1)
        ));
        this.container.addView(this.descriptionText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        final int margin = (int) UnitConverter.fromDpToPx(getContext(), 4.0f);
        params.setMargins(margin, margin, margin, margin);

        this.container.addView(this.confirmation, params);

        setContentView(this.container, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.container.setBackgroundColor(theme.baseColor());
            this.separator.setBackgroundColor(theme.contrastColor());
        }
    }

    public static class Factory implements Dialog.Factory {

        @Override
        public Dialog create(Context context, Parcelable parcelable, DialogEventListener listener) {
            return new ExplainNotificationPermissionDialog(context, parcelable, listener);
        }
    }

    public class ProceedPermissionRequest extends DialogEvent {

    }
    
    public class EmptyParcel implements Parcelable {

        public EmptyParcel() {
            
        }
        
        protected EmptyParcel(Parcel in) {
        }

        public final Creator<EmptyParcel> CREATOR = new Creator<EmptyParcel>() {
            @Override
            public EmptyParcel createFromParcel(Parcel in) {
                return new EmptyParcel(in);
            }

            @Override
            public EmptyParcel[] newArray(int size) {
                return new EmptyParcel[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }
}
