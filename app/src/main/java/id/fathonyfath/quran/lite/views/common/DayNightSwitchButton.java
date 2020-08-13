package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import id.fathonyfath.quran.lite.models.config.DayNightPreference;
import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.UnitConverter;

public class DayNightSwitchButton extends View {

    private final Paint basePaint;
    private final Paint clearPaint;
    private final RectF reusableRect;

    private DayNightPreference currentPreference = DayNightPreference.SYSTEM;

    public DayNightSwitchButton(Context context) {
        super(context);

        initConfiguration();

        this.reusableRect = new RectF();
        this.basePaint = new Paint();
        this.clearPaint = new Paint();

        setPaintColor();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updatePadding();
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

    private void setPaintColor() {
        int colorToApply = Color.parseColor("#FF000000");

        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());

        if (theme != null) {
            colorToApply = theme.contrastColor();
        }

        this.basePaint.setColor(colorToApply);

        final PorterDuff.Mode mode = PorterDuff.Mode.CLEAR;
        this.clearPaint.setXfermode(new PorterDuffXfermode(mode));
    }

    private void initConfiguration() {
        setLayoutParams(new ViewGroup.LayoutParams(
                (int) UnitConverter.fromDpToPx(getContext(), 48f),
                (int) UnitConverter.fromDpToPx(getContext(), 48f)
        ));

        updatePadding();

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
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

    private void drawSun(final Canvas canvas) {
        canvas.save();

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

        final float bottom = (float) (centerY + (radius * Math.sin(Math.toRadians(45))));
        final float right = (float) (centerX + (radius * Math.cos(Math.toRadians(45))));
        final float top = (float) (centerY + (radius * Math.sin(Math.toRadians(225))));
        final float left = (float) (centerX + (radius * Math.cos(Math.toRadians(225))));

        canvas.rotate(-45, centerX, centerY);

        canvas.drawRect(left, top, right, bottom, this.basePaint);

        canvas.restore();

        canvas.drawRect(left, top, right, bottom, this.basePaint);

        final int radiusCircleOutside = (int) (centerX - top);
        final int radiusCircleInside = radiusCircleOutside - (int) UnitConverter.fromDpToPx(getContext(), 2f);

        canvas.drawCircle(centerX, centerY, radiusCircleOutside, this.clearPaint);

        canvas.drawCircle(centerX, centerY, radiusCircleInside, this.basePaint);
    }

    private void drawBrightness(final Canvas canvas) {
        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int centerX = getPaddingLeft() + (availableWidth / 2);
        final int centerY = getPaddingTop() + (availableHeight / 2);
        final int radius;
        if (availableHeight < availableWidth) {
            radius = availableHeight / 2;
        } else {
            radius = availableWidth / 2;
        }
        canvas.drawCircle(centerX, centerY, radius, this.basePaint);

        final int padding = (int) UnitConverter.fromDpToPx(getContext(), 3f);

        this.reusableRect.set((centerX - radius) + padding,
                (centerY - radius) + padding,
                (centerX + radius) - padding,
                (centerY + radius) - padding);
        canvas.drawArc(this.reusableRect, 270f, 180f, false, this.clearPaint);
    }

    private void drawMoon(final Canvas canvas) {
        canvas.save();
        int offsets = (int) UnitConverter.fromDpToPx(getContext(), 4f);
        int radiusOffsets = (int) UnitConverter.fromDpToPx(getContext(), 2f);

        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int centerX = getPaddingLeft() + (availableWidth / 2);
        final int centerY = getPaddingTop() + (availableHeight / 2);
        final int radius;
        if (availableHeight < availableWidth) {
            radius = availableHeight / 2;
        } else {
            radius = availableWidth / 2;
        }

        canvas.rotate(-45, centerX, centerY);
        canvas.drawCircle(centerX, centerY, radius, this.basePaint);

        canvas.drawCircle(centerX + offsets, centerY, radius - radiusOffsets, this.clearPaint);
        canvas.restore();
    }
}
