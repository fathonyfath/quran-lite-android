package id.fathonyfath.quranlite.views.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import id.fathonyfath.quranlite.themes.BaseTheme;
import id.fathonyfath.quranlite.utils.ThemeContext;
import id.fathonyfath.quranlite.utils.UnitConverter;

public class CloseView extends View {

    private final Paint paint;

    private int workingSize = 0;

    private int lineWidth = 0;

    private int horizontalTop = 0;
    private int horizontalLeft = 0;

    private int verticalTop = 0;
    private int verticalLeft = 0;

    public CloseView(Context context) {
        super(context);

        initConfiguration();

        this.paint = new Paint();

        setPaintColor();
    }

    private void setPaintColor() {
        int colorToApply = Color.parseColor("#FF000000");

        BaseTheme theme = ThemeContext.saveUnwrapTheme(getContext());

        if (theme != null) {
            colorToApply = theme.objectOnPrimary();
        }

        this.paint.setColor(colorToApply);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updatePadding();

        int workingHeight = h - getPaddingTop() - getPaddingBottom();
        int workingWidth = w - getPaddingLeft() - getPaddingRight();

        if (workingHeight <= workingWidth) {
            this.workingSize = workingHeight;
        } else {
            this.workingSize = workingWidth;
        }

        this.lineWidth = this.workingSize / 5;

        this.horizontalTop = (h / 2) - (this.lineWidth / 2);
        this.horizontalLeft = (w / 2) - (this.workingSize / 2);

        this.verticalTop = (h / 2) - (this.workingSize / 2);
        this.verticalLeft = (w / 2) - (this.lineWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.rotate(45f, getWidth() / 2, getHeight() / 2);
        canvas.drawRect(
                this.horizontalLeft,
                this.horizontalTop,
                this.horizontalLeft + this.workingSize,
                this.horizontalTop + this.lineWidth,
                this.paint);

        canvas.drawRect(
                this.verticalLeft,
                this.verticalTop,
                this.verticalLeft + this.lineWidth,
                this.verticalTop + this.workingSize,
                this.paint);
        canvas.restore();
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
    }

    private void updatePadding() {
        setPadding(
                (int) UnitConverter.fromDpToPx(getContext(), 12f),
                (int) UnitConverter.fromDpToPx(getContext(), 8f),
                (int) UnitConverter.fromDpToPx(getContext(), 12f),
                (int) UnitConverter.fromDpToPx(getContext(), 8f)
        );
    }
}
