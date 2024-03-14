package dev.fathony.android.quranlite.utils.drawing;

import android.graphics.RectF;

public class Circle {
    public final Vec2 position;
    public final float radius;

    public Circle(Vec2 position, float radius) {
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

    public Vec2 getPointAtAngleDeg(float degree) {
        final double radians = Math.toRadians(degree);
        final float x = (float) (this.position.x + (this.radius * Math.cos(radians)));
        final float y = (float) (this.position.y + (this.radius * Math.sin(radians)));

        return new Vec2(x, y);
    }

    public RectF getBounds() {
        return new RectF(
                this.position.x - this.radius,
                this.position.y - this.radius,
                this.position.x + this.radius,
                this.position.y + this.radius
        );
    }
}
