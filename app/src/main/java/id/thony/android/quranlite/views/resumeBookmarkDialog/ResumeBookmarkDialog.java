package id.thony.android.quranlite.views.resumeBookmarkDialog;

import android.content.Context;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import id.thony.android.quranlite.models.Bookmark;
import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.UnitConverter;
import id.thony.android.quranlite.utils.dialogManager.Dialog;
import id.thony.android.quranlite.utils.dialogManager.DialogEvent;
import id.thony.android.quranlite.utils.dialogManager.DialogEventListener;
import id.thony.android.quranlite.views.common.ButtonView;
import id.thony.android.quranlite.views.common.LpmqTextView;

public class ResumeBookmarkDialog extends Dialog {

    private final LinearLayout container;

    private final LpmqTextView titleText;
    private final View separator;
    private final LpmqTextView descriptionText;

    private final LinearLayout buttonContainer;
    private final ButtonView continueReading;
    private final ButtonView close;

    private final View.OnClickListener onCloseClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ResumeBookmarkDialog.this.dismiss();
        }
    };
    private final View.OnClickListener onContinueReadingClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ResumeBookmarkDialog.this.dismiss();

            final Bookmark bookmark = getBookmark();
            if (bookmark != null) {
                ResumeBookmarkDialog.this.sendDialogEvent(new ResumeBookmarkEvent(), bookmark);
            }
        }
    };

    public ResumeBookmarkDialog(Context context, Parcelable arguments, DialogEventListener listener) {
        super(context, arguments, listener);

        this.container = new LinearLayout(context);

        this.titleText = new LpmqTextView(context);
        this.separator = new View(context);
        this.descriptionText = new LpmqTextView(context);
        this.buttonContainer = new LinearLayout(context);
        this.continueReading = new ButtonView(context);
        this.close = new ButtonView(context);

        initDialog();
        applyStyleBasedOnTheme();
    }

    private Bookmark getBookmark() {
        return getSafeCastArguments();
    }

    private void initDialog() {
        this.container.setOrientation(LinearLayout.VERTICAL);

        String message = "";
        final Bookmark bookmark = getBookmark();
        if (bookmark != null) {
            message = "Lanjutkan membaca dari QS. "
                    + bookmark.getSurahNameInLatin()
                    + " [" + bookmark.getSurahNumber() + "] pada ayat " + bookmark.getLastReadAyah() + "?";
        }

        this.titleText.setText("Penanda ayat");
        this.descriptionText.setText(message);

        this.titleText.setTextSize(20.0f);
        this.titleText.setGravity(Gravity.CENTER_VERTICAL);
        this.titleText.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0
        );

        this.buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        this.buttonContainer.setWeightSum(2);

        this.close.setText("Tutup");
        this.continueReading.setText("Lanjutkan");

        this.close.setOnClickListener(this.onCloseClickListener);
        this.continueReading.setOnClickListener(this.onContinueReadingClickListener);

        int halfPadding = (int) UnitConverter.fromDpToPx(getContext(), 2.0f);

        final LinearLayout.LayoutParams firstParams = new LinearLayout.LayoutParams(
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 48.0f),
                1.0f
        );
        firstParams.setMargins(0, 0, halfPadding, 0);

        final LinearLayout.LayoutParams secondParams = new LinearLayout.LayoutParams(
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 48.0f),
                1.0f
        );
        secondParams.setMargins(halfPadding, 0, 0, 0);

        this.buttonContainer.addView(this.close, firstParams);
        this.buttonContainer.addView(this.continueReading, secondParams);

        int padding = (int) UnitConverter.fromDpToPx(getContext(), 4.0f);
        this.buttonContainer.setPadding(padding, padding, padding, padding);

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
        this.container.addView(this.buttonContainer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

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
            return new ResumeBookmarkDialog(context, parcelable, listener);
        }
    }

    public class ResumeBookmarkEvent extends DialogEvent {

    }
}
