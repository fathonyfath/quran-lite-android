package dev.fathony.android.quranlite.views.ayahDetailDialog;

import android.content.Context;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import dev.fathony.android.quranlite.models.SelectedAyah;
import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;
import dev.fathony.android.quranlite.utils.dialogManager.Dialog;
import dev.fathony.android.quranlite.utils.dialogManager.DialogEvent;
import dev.fathony.android.quranlite.utils.dialogManager.DialogEventListener;
import dev.fathony.android.quranlite.views.common.ButtonView;
import dev.fathony.android.quranlite.views.common.LpmqTextView;

public class AyahDetailDialog extends Dialog {

    private final LinearLayout container;

    private final LpmqTextView titleText;
    private final View separator;
    private final LpmqTextView descriptionText;

    private final ButtonView tafsir;
    private final ButtonView bookmark;

    private final View.OnClickListener onTafsirClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            AyahDetailDialog.this.dismiss();

            final SelectedAyah selectedAyah = getSelectedAyah();
            if (selectedAyah != null) {
                AyahDetailDialog.this.sendDialogEvent(new ReadTafsirEvent(), selectedAyah);
            }
        }
    };
    private final View.OnClickListener onBookmarkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            AyahDetailDialog.this.dismiss();

            final SelectedAyah selectedAyah = getSelectedAyah();
            if (selectedAyah != null) {
                AyahDetailDialog.this.sendDialogEvent(new PutBookmarkEvent(), selectedAyah);
            }
        }
    };

    public AyahDetailDialog(Context context, Parcelable arguments, DialogEventListener listener) {
        super(context, arguments, listener);

        this.container = new LinearLayout(context);

        this.titleText = new LpmqTextView(context);
        this.separator = new View(context);
        this.descriptionText = new LpmqTextView(context);
        this.tafsir = new ButtonView(context);
        this.bookmark = new ButtonView(context);

        initDialog();
        applyStyleBasedOnTheme();
    }

    private void initDialog() {
        this.container.setOrientation(LinearLayout.VERTICAL);

        String title = "";
        final SelectedAyah selectedAyah = getSelectedAyah();
        if (selectedAyah != null) {
            title = "QS. " + selectedAyah.getSurah().getNameInLatin()
                    + " [" + selectedAyah.getSurah().getNumber() + "]: "
                    + selectedAyah.getAyahNumber();
        }

        this.titleText.setText(title);
        this.descriptionText.setText("Silahkan pilih tindakan yang tersedia.");

        this.titleText.setTextSize(20.0f);
        this.titleText.setGravity(Gravity.CENTER_VERTICAL);
        this.titleText.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0,
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                0
        );

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

        this.tafsir.setText("Lihat Tafsir");
        this.bookmark.setText("Tandai");

        this.tafsir.setOnClickListener(this.onTafsirClickListener);
        this.bookmark.setOnClickListener(this.onBookmarkClickListener);

        int padding = (int) UnitConverter.fromDpToPx(getContext(), 4.0f);
        int halfPadding = (int) UnitConverter.fromDpToPx(getContext(), 2.0f);
        final LinearLayout.LayoutParams firstParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        firstParams.setMargins(padding, padding, padding, padding);

        final LinearLayout.LayoutParams secondParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        secondParams.setMargins(padding, halfPadding, padding, padding);

        this.container.addView(this.tafsir, firstParams);
        this.container.addView(this.bookmark, secondParams);

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

    private SelectedAyah getSelectedAyah() {
        return getSafeCastArguments();
    }

    public static class Factory implements Dialog.Factory {

        @Override
        public Dialog create(Context context, Parcelable parcelable, DialogEventListener listener) {
            return new AyahDetailDialog(context, parcelable, listener);
        }
    }

    public class ReadTafsirEvent extends DialogEvent {

    }

    public class PutBookmarkEvent extends DialogEvent {

    }
}
