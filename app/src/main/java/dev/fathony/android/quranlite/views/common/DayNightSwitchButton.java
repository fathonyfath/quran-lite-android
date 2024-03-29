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

import dev.fathony.android.quranlite.models.config.DayNightPreference;
import dev.fathony.android.quranlite.themes.BaseTheme;
import dev.fathony.android.quranlite.utils.ThemeContext;
import dev.fathony.android.quranlite.utils.UnitConverter;
import dev.fathony.android.quranlite.utils.ViewUtil;
import dev.fathony.android.quranlite.utils.drawing.Circle;
import dev.fathony.android.quranlite.utils.drawing.RectHelper;
import dev.fathony.android.quranlite.utils.drawing.Vec2;

public class DayNightSwitchButton extends View {

    private final Paint basePaint;

    private final Rect workingSpace;

    private final Path brightnessLeftPath;
    private final Path brightnessRightPath;
    private final Path raysPath;
    private final Path moonPath;

    private DayNightPreference currentPreference = DayNightPreference.SYSTEM;

    public DayNightSwitchButton(Context context) {
        super(context);

        this.basePaint = new Paint();

        this.workingSpace = new Rect();

        this.brightnessLeftPath = new Path();
        this.brightnessRightPath = new Path();
        this.raysPath = new Path();
        this.moonPath = new Path();

        initConfiguration();
        applyColorFromTheme();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updatePadding();
        updateWorkingSpace();
        updateBrightnessPath();
        updateSunPath();
        updateMoonPath();
    }

    public void setDayNightPreference(DayNightPreference preference) {
        if (preference == null) {
            preference = DayNightPreference.SYSTEM;
        }
        this.currentPreference = preference;
    }

    public DayNightPreference cycleNextPreference() {
        switch (this.currentPreference) {
            case SYSTEM:
                return DayNightPreference.DAY;
            case DAY:
                return DayNightPreference.NIGHT;
            case NIGHT:
                return DayNightPreference.SYSTEM;
        }

        throw new IllegalStateException("This is impossible. Throw exceptions!");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (this.currentPreference) {
            case SYSTEM:
                drawBrightness(canvas);
                break;
            case DAY:
                drawSun(canvas);
                break;
            case NIGHT:
                drawMoon(canvas);
                break;
        }
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
        updateBrightnessPath();
        updateSunPath();
        updateMoonPath();

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

    private void drawBrightness(final Canvas canvas) {
        canvas.drawPath(this.brightnessRightPath, this.basePaint);
        canvas.drawPath(this.brightnessLeftPath, this.basePaint);
    }

    private void updateBrightnessPath() {
        final float differenceRadius = UnitConverter.fromDpToPx(getContext(), 3f);

        final Circle outerCircle = RectHelper.findCircleOnRect(this.workingSpace, getMeasuredHeight(), getMeasuredWidth());
        final Circle innerCircle = new Circle(
                new Vec2(outerCircle.position.x, outerCircle.position.y),
                outerCircle.radius - differenceRadius
        );

        this.brightnessRightPath.reset();
        this.brightnessRightPath.arcTo(outerCircle.getBounds(), 270.0f, 180.0f);
        this.brightnessRightPath.arcTo(innerCircle.getBounds(), 90.0f, -180.0f);
        this.brightnessRightPath.close();

        this.brightnessLeftPath.reset();
        this.brightnessLeftPath.arcTo(outerCircle.getBounds(), 90.0f, 180.0f);
        this.brightnessLeftPath.close();
    }

    private void drawSun(final Canvas canvas) {
        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int radius;
        if (availableHeight < availableWidth) {
            radius = availableHeight / 2;
        } else {
            radius = availableWidth / 2;
        }
        final int centerX = getPaddingLeft() + (availableWidth / 2);
        final int centerY = getPaddingTop() + (availableHeight / 2);

        final float padding = UnitConverter.fromDpToPx(getContext(), 5.5f);

        canvas.save();
        for (int i = 0; i < 8; i++) {
            canvas.rotate(i * 45.0f, centerX, centerY);
            canvas.drawPath(this.raysPath, this.basePaint);
        }
        canvas.restore();

        canvas.drawCircle(centerX, centerY, radius - padding, this.basePaint);

    }

    private void updateSunPath() {
        final float raysSize = UnitConverter.fromDpToPx(getContext(), 4.0f);

        Circle circle = RectHelper.findCircleOnRect(this.workingSpace, getMeasuredHeight(), getMeasuredWidth());
        Circle raysCircle = new Circle(new Vec2(circle.position.x, circle.position.y), circle.radius - raysSize);

        Vec2 topTriangle = circle.getPointAtAngleDeg((float) 0);
        Vec2 leftLeg = raysCircle.getPointAtAngleDeg((float) 0 - 35.0f);
        Vec2 rightLeg = raysCircle.getPointAtAngleDeg((float) 0 + 35.0f);

        raysPath.reset();
        raysPath.moveTo(rightLeg.x, rightLeg.y);
        raysPath.lineTo(topTriangle.x, topTriangle.y);
        raysPath.lineTo(leftLeg.x, leftLeg.y);
        raysPath.arcTo(raysCircle.getBounds(), (float) 0 - 22.5f, 45.0f);
        raysPath.close();
    }

    private void drawMoon(final Canvas canvas) {
        canvas.save();

        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int centerX = getPaddingLeft() + (availableWidth / 2);
        final int centerY = getPaddingTop() + (availableHeight / 2);

        canvas.rotate(-45, centerX, centerY);
        canvas.drawPath(this.moonPath, this.basePaint);

        canvas.restore();
    }

    private void updateMoonPath() {

        int offsets = (int) UnitConverter.fromDpToPx(getContext(), 4f);
        int radiusOffsets = (int) UnitConverter.fromDpToPx(getContext(), 2f);

        final RectF innerCircleRect = RectHelper.findSquareRect(this.workingSpace, getMeasuredHeight(), getMeasuredWidth());
        final Circle innerCircle = RectHelper.findCircleOnRect(this.workingSpace, getMeasuredHeight(), getMeasuredWidth());

        final Rect outerCircleRectContainer = new Rect(this.workingSpace);
        outerCircleRectContainer.left += offsets;
        outerCircleRectContainer.right += offsets;

        outerCircleRectContainer.top += radiusOffsets;
        outerCircleRectContainer.bottom -= radiusOffsets;
        outerCircleRectContainer.left += radiusOffsets;
        outerCircleRectContainer.right -= radiusOffsets;

        final RectF outerCircleRect = RectHelper.findSquareRect(outerCircleRectContainer, getMeasuredHeight(), getMeasuredWidth());
        final Circle outerCircle = RectHelper.findCircleOnRect(outerCircleRectContainer, getMeasuredHeight(), getMeasuredWidth());

        double degreeFromInnerCircle = innerCircle.degrees(outerCircle);
        double degreeFromOuterCircle = outerCircle.degrees(innerCircle);

        this.moonPath.reset();
        this.moonPath.arcTo(innerCircleRect, (float) degreeFromInnerCircle, 360.0f - ((float) (degreeFromInnerCircle * 2.0)));
        if (degreeFromOuterCircle > 0.0) {
            this.moonPath.arcTo(outerCircleRect, (float) (180.0f + degreeFromOuterCircle), (float) -(degreeFromOuterCircle * 2.0));
        } else {
            this.moonPath.arcTo(outerCircleRect, (float) (360.0f + degreeFromOuterCircle), (float) -(360.0f + (degreeFromOuterCircle * 2.0)));
        }
        this.moonPath.close();
    }


}
