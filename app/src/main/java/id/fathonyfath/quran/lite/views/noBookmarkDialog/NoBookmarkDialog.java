package id.fathonyfath.quran.lite.views.noBookmarkDialog;

import android.content.Context;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.UnitConverter;
import id.fathonyfath.quran.lite.utils.dialogManager.Dialog;
import id.fathonyfath.quran.lite.views.common.ButtonView;
import id.fathonyfath.quran.lite.views.common.LpmqTextView;

public class NoBookmarkDialog extends Dialog {

    private final LinearLayout container;

    private final LpmqTextView titleText;
    private final View separator;
    private final LpmqTextView descriptionText;
    private final ButtonView confirmation;

    private final View.OnClickListener onConfirmationClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            NoBookmarkDialog.this.dismiss();
        }
    };

    public NoBookmarkDialog(Context context, Parcelable arguments) {
        super(context, arguments);

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

        this.titleText.setText("Penanda ayat tidak ditemukan");
        this.descriptionText.setText("Tambahkan penanda ayat dengan cara tekan-tahan pada ayat yang ingin anda tandai.");

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
        public Dialog create(Context context, Parcelable parcelable) {
            return new NoBookmarkDialog(context, parcelable);
        }
    }
}
