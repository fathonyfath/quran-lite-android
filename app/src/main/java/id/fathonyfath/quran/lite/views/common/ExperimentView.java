package id.fathonyfath.quran.lite.views.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import id.fathonyfath.quran.lite.themes.BaseTheme;
import id.fathonyfath.quran.lite.utils.ThemeContext;
import id.fathonyfath.quran.lite.utils.UnitConverter;
import id.fathonyfath.quran.lite.utils.ViewUtil;

public class ExperimentView extends View {

    private final Paint paint;

    private final Rect workingSpace = new Rect();
    private final Path path = new Path();

    private final Path[] raysPath = new Path[8];

    public ExperimentView(Context context) {
        super(context);

        initConfiguration();

        this.paint = new Paint();

        setPaintColor();
    }

    private void setPaintColor() {
        int colorToApply = Color.parseColor("#FF000000");

        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());

        if (theme != null) {
            colorToApply = theme.contrastColor();
        }

        this.paint.setColor(colorToApply);
        ViewUtil.setDefaultSelectableBackgroundDrawable(this, colorToApply);
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

        for (Path value : this.raysPath) {
            canvas.drawPath(value, this.paint);
        }
    }

    private void initConfiguration() {
        setLayoutParams(new ViewGroup.LayoutParams(
                (int) UnitConverter.fromDpToPx(getContext(), 48f),
                (int) UnitConverter.fromDpToPx(getContext(), 48f)
        ));

        updatePadding();
        updateWorkingSpace();
        updatePath();

        setClickable(true);
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
        for(int i = 0; i < this.raysPath.length; i++) {
            this.raysPath[i] = generatePathForRays(45.0f * i);
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
}

class RectHelper {
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

class Vec2 {
    public final float x, y;

    Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }
}

class Circle {
    public final Vec2 position;
    public final float radius;

    Circle(Vec2 position, float radius) {
        this.position = position;
        this.radius = radius;
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
