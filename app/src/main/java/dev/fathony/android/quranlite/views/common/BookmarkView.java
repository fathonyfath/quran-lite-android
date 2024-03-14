package dev.fathony.android.quranlite.views.common;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import dev.fathony.android.quranlite.models.Bookmark;
import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;
import dev.fathony.android.quranlite.utils.ViewUtil;

public class BookmarkView extends LinearLayout {

    private final LpmqTextView bookmarkText;
    private final BookmarkIconView bookmarkIcon;

    private Bookmark bookmark;

    public BookmarkView(Context context) {
        super(context);

        this.bookmarkText = new LpmqTextView(context);
        this.bookmarkIcon = new BookmarkIconView(context);

        setBookmark(null);

        initConfiguration();
        initView();
        applyStyleBasedOnTheme();
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public void setBookmark(Bookmark bookmark) {
        this.bookmark = bookmark;

        if (bookmark == null) {
            this.bookmarkText.setVisibility(View.GONE);
            return;
        }

        this.bookmarkText.setVisibility(View.VISIBLE);
        this.bookmarkText.setText("QS. " + bookmark.getSurahNumber() + ": " + bookmark.getLastReadAyah());
    }

    private void initConfiguration() {
        setOrientation(LinearLayout.HORIZONTAL);

        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) UnitConverter.fromDpToPx(getContext(), 48f)
        ));

        setClickable(true);
    }

    private void initView() {
        LinearLayout.LayoutParams firstParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        LinearLayout.LayoutParams secondParams = new LayoutParams(
                (int) UnitConverter.fromDpToPx(getContext(), 48f),
                (int) UnitConverter.fromDpToPx(getContext(), 48f)
        );

        firstParams.gravity = Gravity.CENTER_VERTICAL;
        secondParams.gravity = Gravity.CENTER_VERTICAL;

        addView(this.bookmarkText, firstParams);
        addView(this.bookmarkIcon, secondParams);

        this.bookmarkText.setGravity(Gravity.CENTER_VERTICAL);
        this.bookmarkText.setPadding((int) UnitConverter.fromDpToPx(getContext(), 16f), 0, 0, 0);
        this.bookmarkText.setTextSize(16f);
    }

    private void applyStyleBasedOnTheme() {
        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());
        if (theme != null) {
            this.bookmarkText.setTextColor(theme.contrastColor());
            ViewUtil.setDefaultSelectableBackgroundDrawable(this, theme.contrastColor());
        }
    }
}
