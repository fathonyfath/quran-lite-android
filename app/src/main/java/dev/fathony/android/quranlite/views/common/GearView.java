package dev.fathony.android.quranlite.views.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;
import dev.fathony.android.quranlite.utils.ViewUtil;
import dev.fathony.android.quranlite.utils.drawing.Circle;
import dev.fathony.android.quranlite.utils.drawing.RectHelper;
import dev.fathony.android.quranlite.utils.drawing.Vec2;

public class GearView extends View {

    private final Paint basePaint;

    private final Rect workingSpace;

    private final Path gearLeftPath;
    private final Path gearRightPath;
    private final Path gearPath;

    public GearView(Context context) {
        super(context);

        this.basePaint = new Paint();

        this.workingSpace = new Rect();

        this.gearLeftPath = new Path();
        this.gearRightPath = new Path();
        this.gearPath = new Path();

        initConfiguration();
        applyColorFromTheme();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updatePadding();
        updateWorkingSpace();
        updateGearPath();
    }

    @Override
    protected void onDraw(@NotNull Canvas canvas) {
        super.onDraw(canvas);

        drawGear(canvas);
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
        updateGearPath();

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

    private void updateGearPath() {
        final float differenceRadius = UnitConverter.fromDpToPx(getContext(), 4f);
        final float gearThickness = UnitConverter.fromDpToPx(getContext(), 2.5f);
        final float outerCircleOffset = UnitConverter.fromDpToPx(getContext(), 3f);

        final Rect circleRect = new Rect(this.workingSpace);
        circleRect.left += (int) outerCircleOffset;
        circleRect.right -= (int) outerCircleOffset;

        final Circle outerCircle = RectHelper.findCircleOnRect(circleRect, getMeasuredHeight(), getMeasuredWidth());
        final Circle innerCircle = new Circle(
                new Vec2(outerCircle.position.x, outerCircle.position.y),
                outerCircle.radius - differenceRadius
        );

        this.gearRightPath.reset();
        this.gearRightPath.arcTo(outerCircle.getBounds(), 270.0f, 180.0f);
        this.gearRightPath.arcTo(innerCircle.getBounds(), 90.0f, -180.0f);
        this.gearRightPath.close();

        this.gearLeftPath.reset();
        this.gearLeftPath.arcTo(outerCircle.getBounds(), 90.0f, 180.0f);
        this.gearLeftPath.arcTo(innerCircle.getBounds(), 270.0f, -180.0f);
        this.gearLeftPath.close();

        final float spaceBetweenCircle = (innerCircle.getBounds().top - outerCircle.getBounds().top) / 2.0f;
        final float outerCircleCenterX = outerCircle.position.x;

        final float bottom = outerCircle.getBounds().top + spaceBetweenCircle;
        final float left = outerCircleCenterX - gearThickness;
        final float right = outerCircleCenterX + gearThickness;
        final float top = Math.max(workingSpace.top, workingSpace.left);

        final RectF gearRect = new RectF();
        gearRect.bottom = bottom;
        gearRect.left = left;
        gearRect.right = right;
        gearRect.top = top;

        this.gearPath.reset();
        this.gearPath.moveTo(gearRect.left, gearRect.bottom);
        this.gearPath.lineTo(gearRect.right, gearRect.bottom);
        this.gearPath.lineTo(gearRect.right, gearRect.top);
        this.gearPath.lineTo(gearRect.left, gearRect.top);
        this.gearPath.lineTo(gearRect.left, gearRect.bottom);
        this.gearPath.close();
    }

    private void drawGear(Canvas canvas) {
        canvas.save();
        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int centerX = getPaddingLeft() + (availableWidth / 2);
        final int centerY = getPaddingTop() + (availableHeight / 2);

        for (int i = 0; i < 8; i++) {
            canvas.rotate(i * 45, centerX, centerY);
            canvas.drawPath(this.gearPath, this.basePaint);
        }
        canvas.restore();

        canvas.drawPath(this.gearLeftPath, this.basePaint);
        canvas.drawPath(this.gearRightPath, this.basePaint);
    }
}
