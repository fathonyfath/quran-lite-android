package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import id.fathonyfath.quran.lite.models.config.DayNightPreference;
import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.UnitConverter;
import id.fathonyfath.quran.lite.utils.ViewUtil;

public class DayNightSwitchButton extends View {

    private final Paint basePaint;

    private final Rect workingSpace;

    private final Path brightnessLeftPath;
    private final Path brightnessRightPath;
    private final Path[] raysPaths;
    private final Path moonPath;

    private DayNightPreference currentPreference = DayNightPreference.SYSTEM;

    public DayNightSwitchButton(Context context) {
        super(context);

        this.basePaint = new Paint();

        this.workingSpace = new Rect();

        this.brightnessLeftPath = new Path();
        this.brightnessRightPath = new Path();
        this.raysPaths = new Path[8];
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

        setLayerType(LAYER_TYPE_HARDWARE, null);
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
        this.brightnessRightPath.arcTo(outerCircle.getBounds(), 90.0f, 180.0f);
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

        canvas.drawCircle(centerX, centerY, radius - padding, this.basePaint);

        for (Path path : this.raysPaths) {
            canvas.drawPath(path, this.basePaint);
        }
    }

    private void updateSunPath() {
        for (int i = 0; i < this.raysPaths.length; i++) {
            this.raysPaths[i] = generatePathForRays(i * 45.0f);
        }
    }

    private Path generatePathForRays(float degree) {
        final Path path = new Path();

        final float raysSize = UnitConverter.fromDpToPx(getContext(), 4.0f);

        Circle circle = RectHelper.findCircleOnRect(this.workingSpace, getMeasuredHeight(), getMeasuredWidth());
        Circle raysCircle = new Circle(new Vec2(circle.position.x, circle.position.y), circle.radius - raysSize);

        Vec2 topTriangle = circle.getPointAtAngleDeg(degree);
        Vec2 leftLeg = raysCircle.getPointAtAngleDeg(degree - 35.0f);
        Vec2 rightLeg = raysCircle.getPointAtAngleDeg(degree + 35.0f);

        path.reset();
        path.moveTo(rightLeg.x, rightLeg.y);
        path.lineTo(topTriangle.x, topTriangle.y);
        path.lineTo(leftLeg.x, leftLeg.y);
        path.arcTo(raysCircle.getBounds(), degree - 22.5f, 45.0f);
        path.close();

        return path;
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

    private static final class RectHelper {
        public static RectF findSquareRect(Rect rect, float containerHeight, float containerWidth) {
            final float availableHeight = rect.bottom - rect.top;
            final float availableWidth = rect.right - rect.left;

            final RectF circleRect = new RectF();

            if (availableHeight < availableWidth) {
                final float left = (containerWidth - availableHeight) / 2.0f;
                final float right = left + availableHeight;

                circleRect.set(
                        left,
                        rect.top,
                        right,
                        rect.bottom
                );
            } else {
                final float top = (containerHeight - availableWidth) / 2.0f;
                final float bottom = top + availableWidth;

                circleRect.set(
                        rect.left,
                        top,
                        rect.right,
                        bottom
                );
            }

            return circleRect;
        }

        public static Circle findCircleOnRect(Rect rect, float containerHeight, float containerWidth) {
            final RectF circleRect = findSquareRect(rect, containerHeight, containerWidth);

            final float diameter = circleRect.bottom - circleRect.top;
            final float radius = diameter / 2.0f;
            final float x = circleRect.left + radius;
            final float y = circleRect.top + radius;

            return new Circle(new Vec2(x, y), radius);
        }
    }

    private static class Vec2 {
        public final float x, y;

        Vec2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float distance(Vec2 other) {
            return (float) Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
        }
    }

    private static final class Circle {
        public final Vec2 position;
        public final float radius;

        Circle(Vec2 position, float radius) {
            this.position = position;
            this.radius = radius;
        }

        public double degrees(Circle other) {
            final float d = position.distance(other.position);
            final float powOfThisR = (float) Math.pow(this.radius, 2);
            final float powOfOtherR = (float) Math.pow(other.radius, 2);
            final float powOfD = (float) Math.pow(d, 2);
            final float a = (powOfThisR - powOfOtherR + powOfD) / (2 * d);
            final float h = (float) Math.sqrt(powOfThisR - (a * a));

            final double radians = Math.atan(h / a);

            return Math.toDegrees(radians);
        }

        Vec2 getPointAtAngleDeg(float degree) {
            final double radians = Math.toRadians(degree);
            final float x = (float) (this.position.x + (this.radius * Math.cos(radians)));
            final float y = (float) (this.position.y + (this.radius * Math.sin(radians)));

            return new Vec2(x, y);
        }

        RectF getBounds() {
            return new RectF(
                    this.position.x - this.radius,
                    this.position.y - this.radius,
                    this.position.x + this.radius,
                    this.position.y + this.radius
            );
        }
    }
}
