package id.thony.android.quranlite.views.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import id.thony.android.quranlite.themes.BaseTheme;
import id.thony.android.quranlite.utils.ThemeContext;
import id.thony.android.quranlite.utils.UnitConverter;
import id.thony.android.quranlite.utils.ViewUtil;

public class DownloadView extends View {

    private final Paint basePaint;

    private final Rect workingSpace;

    private final Path path;

    public DownloadView(Context context) {
        super(context);

        this.basePaint = new Paint();

        this.workingSpace = new Rect();

        this.path = new Path();

        initConfiguration();
        applyColorFromTheme();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updatePadding();
        updateWorkingSpace();
        updateGlassesPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGlassesSearch(canvas);
    }

    private void applyColorFromTheme() {
        int colorToApply = Color.parseColor("#FF000000");

        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());

        if (theme != null) {
            colorToApply = theme.contrastColor();
        }

        this.basePaint.setColor(colorToApply);
        ViewUtil.setDefaultSelectableBackgroundDrawable(this, colorToApply);
    }

    private void initConfiguration() {
        setLayoutParams(new ViewGroup.LayoutParams(
                (int) UnitConverter.fromDpToPx(getContext(), 48f),
                (int) UnitConverter.fromDpToPx(getContext(), 48f)
        ));

        updatePadding();
        updateWorkingSpace();
        updateGlassesPath();

        setClickable(true);

        this.basePaint.setAntiAlias(true);
    }

    private void updatePadding() {
        setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 12f),
                (int) UnitConverter.fromDpToPx(getContext(), 12f),
                (int) UnitConverter.fromDpToPx(getContext(), 12f),
                (int) UnitConverter.fromDpToPx(getContext(), 12f)
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

    private void updateGlassesPath() {
        final int tailThickness = (int) UnitConverter.fromDpToPx(getContext(), 8.0f);
        final int arrowHeight = (int) UnitConverter.fromDpToPx(getContext(), 14.0f);

        final int centerHorizontal = this.workingSpace.left + ((this.workingSpace.right - this.workingSpace.left) / 2);
        final Rect tailRect = new Rect(
                centerHorizontal - (tailThickness / 2),
                this.workingSpace.top,
                centerHorizontal + (tailThickness / 2),
                this.workingSpace.bottom - arrowHeight
        );

        this.path.reset();

        this.path.moveTo(tailRect.left, tailRect.top);
        this.path.lineTo(tailRect.right, tailRect.top);
        this.path.lineTo(tailRect.right, tailRect.bottom);
        this.path.lineTo(this.workingSpace.right, tailRect.bottom);
        this.path.lineTo(centerHorizontal, this.workingSpace.bottom);
        this.path.lineTo(this.workingSpace.left, tailRect.bottom);
        this.path.lineTo(tailRect.left, tailRect.bottom);
        this.path.lineTo(tailRect.left, tailRect.top);

        this.path.close();
    }

    private void drawGlassesSearch(final Canvas canvas) {
        canvas.drawPath(this.path, this.basePaint);
    }
}
