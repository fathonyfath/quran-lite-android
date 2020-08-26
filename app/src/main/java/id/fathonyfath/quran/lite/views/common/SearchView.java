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
import id.fathonyfath.quran.lite.utils.ViewUtil;
import id.fathonyfath.quran.lite.utils.drawing.Circle;
import id.fathonyfath.quran.lite.utils.drawing.RectHelper;
import id.fathonyfath.quran.lite.utils.drawing.Vec2;

public class SearchView extends View {

    private final Paint basePaint;

    private final Rect workingSpace;

    private final Path glassesLeftPath;
    private final Path glassesRightPath;
    private final Path handlePath;

    public SearchView(Context context) {
        super(context);

        this.basePaint = new Paint();

        this.workingSpace = new Rect();

        this.glassesLeftPath = new Path();
        this.glassesRightPath = new Path();
        this.handlePath = new Path();

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

    private void updateGlassesPath() {
        final float differenceRadius = UnitConverter.fromDpToPx(getContext(), 3f);
        final float handleThickness = UnitConverter.fromDpToPx(getContext(), 4f);
        final float horizontalSpace = this.workingSpace.right - this.workingSpace.left;

        final Rect circleRect = new Rect(this.workingSpace);
        circleRect.right -= horizontalSpace / 4.0f;

        final Circle outerCircle = RectHelper.findCircleOnRect(circleRect, getMeasuredHeight(), getMeasuredWidth());
        final Circle innerCircle = new Circle(
                new Vec2(outerCircle.position.x, outerCircle.position.y),
                outerCircle.radius - differenceRadius
        );

        this.glassesRightPath.reset();
        this.glassesRightPath.arcTo(outerCircle.getBounds(), 270.0f, 180.0f);
        this.glassesRightPath.arcTo(innerCircle.getBounds(), 90.0f, -180.0f);
        this.glassesRightPath.close();

        this.glassesLeftPath.reset();
        this.glassesLeftPath.arcTo(outerCircle.getBounds(), 90.0f, 180.0f);
        this.glassesLeftPath.arcTo(innerCircle.getBounds(), 270.0f, -180.0f);
        this.glassesLeftPath.close();

        final Rect handleRect = new Rect(this.workingSpace);
        float centerVertical = handleRect.top + ((handleRect.bottom - handleRect.top) / 2.0f);
        handleRect.top = (int) (centerVertical - (handleThickness / 2.0f));
        handleRect.bottom = (int) (centerVertical + (handleThickness / 2.0f));
        handleRect.left += 3.0f * horizontalSpace / 4.0f;
        handleRect.left -= differenceRadius / 2.0f;

        this.handlePath.reset();
        this.handlePath.moveTo(handleRect.left, handleRect.top);
        this.handlePath.lineTo(handleRect.right, handleRect.top);
        this.handlePath.lineTo(handleRect.right, handleRect.bottom);
        this.handlePath.lineTo(handleRect.left, handleRect.bottom);
        this.handlePath.lineTo(handleRect.left, handleRect.top);
        this.handlePath.close();
    }

    private void drawGlassesSearch(final Canvas canvas) {
        canvas.save();

        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int centerX = getPaddingLeft() + (availableWidth / 2);
        final int centerY = getPaddingTop() + (availableHeight / 2);

        canvas.rotate(45, centerX, centerY);
        canvas.drawPath(this.glassesLeftPath, this.basePaint);
        canvas.drawPath(this.glassesRightPath, this.basePaint);
        canvas.drawPath(this.handlePath, this.basePaint);
        canvas.restore();
    }
}
