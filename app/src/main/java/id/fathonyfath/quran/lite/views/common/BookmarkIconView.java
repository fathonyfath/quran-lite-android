package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.UnitConverter;

public class BookmarkIconView extends View {

    private final Paint paint;

    private final Rect workingSpace = new Rect();
    private final Path bookmarkPath = new Path();

    public BookmarkIconView(Context context) {
        super(context);

        initConfiguration();

        this.paint = new Paint();
        this.paint.setAntiAlias(true);

        setPaintColor();
    }

    private void setPaintColor() {
        int colorToApply = Color.parseColor("#FF000000");

        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());

        if (theme != null) {
            colorToApply = theme.contrastColor();
        }

        this.paint.setColor(colorToApply);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updatePadding();
        updateWorkingSpace();
        updatePath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(this.bookmarkPath, this.paint);
    }

    private void initConfiguration() {
        setLayoutParams(new ViewGroup.LayoutParams(
                (int) UnitConverter.fromDpToPx(getContext(), 48f),
                (int) UnitConverter.fromDpToPx(getContext(), 48f)
        ));

        updatePadding();
        updateWorkingSpace();
        updatePath();
    }

    private void updatePadding() {
        setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 12f),
                (int) UnitConverter.fromDpToPx(getContext(), 8f),
                (int) UnitConverter.fromDpToPx(getContext(), 12f),
                (int) UnitConverter.fromDpToPx(getContext(), 8f)
        );
    }

    private void updateWorkingSpace() {
        workingSpace.set(
                getPaddingLeft(),
                getPaddingTop(),
                getMeasuredWidth() - getPaddingRight(),
                getMeasuredHeight() - getPaddingBottom()
        );
    }

    private void updatePath() {
        this.bookmarkPath.reset();

        final float addedPaddingHorizontal = UnitConverter.fromDpToPx(getContext(), 4f);
        final float addedPaddingVertical = UnitConverter.fromDpToPx(getContext(), 3f);
        final float tailSize = UnitConverter.fromDpToPx(getContext(), 8f);

        final Rect size = new Rect(this.workingSpace);
        size.top += addedPaddingVertical;
        size.bottom -= addedPaddingVertical;
        size.left += addedPaddingHorizontal;
        size.right -= addedPaddingHorizontal;

        final float middleWorkingSpace = size.left + ((float) (size.right - size.left) / 2f);
        final float tailLength = size.bottom - tailSize;

        this.bookmarkPath.moveTo(size.left, size.top);
        this.bookmarkPath.lineTo(size.right, size.top);
        this.bookmarkPath.lineTo(size.right, size.bottom);
        this.bookmarkPath.lineTo(middleWorkingSpace, tailLength);
        this.bookmarkPath.lineTo(size.left, size.bottom);
        this.bookmarkPath.lineTo(size.left, size.top);

        this.bookmarkPath.close();
    }
}
