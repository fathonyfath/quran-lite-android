package dev.fathony.android.quranlite.utils.drawing;

public class Vec2 {
    public final float x, y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float distance(Vec2 other) {
        return (float) Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }
}
