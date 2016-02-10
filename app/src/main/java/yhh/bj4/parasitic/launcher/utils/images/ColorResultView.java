package yhh.bj4.parasitic.launcher.utils.images;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yenhsunhuang on 2016/2/9.
 */
public class ColorResultView extends View {
    private static final int ROUND_CORNER_BASE_VALUE = 5;
    private final Paint mPaint = new Paint();
    private int mColor;
    private final float mRound;

    public ColorResultView(Context context) {
        this(context, null);
    }

    public ColorResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setAntiAlias(true);
        mRound = ROUND_CORNER_BASE_VALUE * context.getResources().getDisplayMetrics().density;
    }

    public void setARGB(int a, int r, int g, int b) {
        mColor = Color.argb(a, r, g, b);
        mPaint.setColor(mColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRoundRect(rect, mRound, mRound, mPaint);
    }
}
