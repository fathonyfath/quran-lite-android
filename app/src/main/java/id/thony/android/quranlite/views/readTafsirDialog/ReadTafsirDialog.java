package id.thony.android.quranlite.views.readTafsirDialog;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import id.thony.android.quranlite.models.SelectedTafsir;
import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.UnitConverter;
import id.thony.android.quranlite.utils.dialogManager.Dialog;
import id.thony.android.quranlite.utils.dialogManager.DialogEventListener;
import id.thony.android.quranlite.views.common.LpmqTextView;

public class ReadTafsirDialog extends Dialog {

    private final ScrollView containerScroller;
    private final LinearLayout container;

    private final LpmqTextView surahNameText;
    private final LpmqTextView tafsirOwnerText;
    private final View separator;
    private final LpmqTextView tafsirText;

    public ReadTafsirDialog(Context context, Parcelable arguments, DialogEventListener listener) {
        super(context, arguments, listener);

        this.containerScroller = new ScrollView(context);
        this.container = new LinearLayout(context);

        this.surahNameText = new LpmqTextView(context);
        this.tafsirOwnerText = new LpmqTextView(context);
        this.separator = new View(context);
        this.tafsirText = new LpmqTextView(context);

        initDialog();
        applyStyleBasedOnTheme();
    }

    private SelectedTafsir getSelectedTafsir() {
        return getSafeCastArguments();
    }

    private void initDialog() {
        this.container.setOrientation(LinearLayout.VERTICAL);

        this.surahNameText.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 12.0f),
                (int) UnitConverter.fromDpToPx(getContext(), 12.0f),
                (int) UnitConverter.fromDpToPx(getContext(), 12.0f),
                (int) UnitConverter.fromDpToPx(getContext(), -4.0f)
        );
        this.surahNameText.setTextSize(18.0f);

        this.tafsirOwnerText.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 12.0f),
                (int) UnitConverter.fromDpToPx(getContext(), -4.0f),
                (int) UnitConverter.fromDpToPx(getContext(), 12.0f),
                (int) UnitConverter.fromDpToPx(getContext(), 12.0f)
        );
        this.tafsirOwnerText.setTextSize(10.0f);


        this.tafsirText.setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f),
                (int) UnitConverter.fromDpToPx(getContext(), 16.0f)
        );
        this.tafsirText.setTextSize(16f);
        this.tafsirText.setTypeface(this.tafsirText.getTypeface(), Typeface.ITALIC);


        this.surahNameText.setText("Tafsir QS. " + getSelectedTafsir().getSurah().getNameInLatin()
                + " [" + getSelectedTafsir().getAyahNumber() + "]");
        this.tafsirOwnerText.setText("Oleh " + getSelectedTafsir().getTafsirName() + " - " + getSelectedTafsir().getTafsirSource());
        this.tafsirText.setText(getSelectedTafsir().getTafsir());

        this.container.addView(this.surahNameText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        this.container.addView(this.tafsirOwnerText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        this.container.addView(this.separator, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) UnitConverter.fromDpToPx(getContext(), 1)
        ));

        this.container.addView(this.tafsirText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        this.containerScroller.addView(this.container);

        setContentView(this.containerScroller, new ViewGroup.LayoutParams(
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

    @Override
    protected ViewGroup.LayoutParams getLayoutParams() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        if (getWindow() != null) {
            Point point = new Point();
            getWindow().getWindowManager().getDefaultDisplay().getSize(point);
            if (point.x > point.y) {
                // This is where we have landscape state phone
                // We change the height to be 75% of its current value
                float widthFloat = point.x * 0.75f;
                width = (int) widthFloat;
            } else {
                // This is where we have portrait state phone
                // We change the width to be 75% of its current value
                float heightFloat = point.y * 0.75f;
                height = (int) heightFloat;
            }
        }

        return new LinearLayout.LayoutParams(width, height);
    }

    public static class Factory implements Dialog.Factory {

        @Override
        public Dialog create(Context context, Parcelable parcelable, DialogEventListener listener) {
            return new ReadTafsirDialog(context, parcelable, listener);
        }
    }
}
