package id.fathonyfath.quran.lite.utils.drawing;

import android.graphics.Rect;
import android.graphics.RectF;

public class RectHelper {
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
