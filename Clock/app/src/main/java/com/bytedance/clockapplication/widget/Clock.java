package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }

    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.08f);
        textPaint.setColor(DEFAULT_PRIMARY_COLOR);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        // - hoursValuesColor
        int rPadded = mCenterX - (int) (mWidth * 0.12f);
        Paint.FontMetrics fontMetrics= textPaint.getFontMetrics();
        float top =fontMetrics.top;
        float bottom=fontMetrics.bottom;
        for (int i = 0,j=90; i < FULL_ANGLE; i += 30) {
            int hour = j/ 30;
            j=j-30;
            if(j==0)j=360;
            if (hour == 0) hour = 12;
            String hourText=String.format("%s",String.format(Locale.getDefault(), "%02d", hour));
            int CenterX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int CenterY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));
            int baseLineY=(int) (CenterY-top/2-bottom/2);
            canvas.drawText(hourText,CenterX,baseLineY,textPaint);
        }


    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int endX=mCenterX;
        int endY=mCenterY;
        // Default Color:
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setStrokeCap(Paint.Cap.ROUND);
        paint1.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH*0.5f);
        paint1.setColor(secondsNeedleColor);
        float angle1=(30-second)*6;
        int rPadded1 = mCenterX - (int) (mWidth * 0.20f);
        int startX1=(int) (mCenterX + rPadded1 * Math.sin(Math.toRadians(angle1)));
        int startY1=(int) (mCenterX + rPadded1 * Math.cos(Math.toRadians(angle1)));
        canvas.drawLine(startX1, startY1, endX, endY, paint1);
        // - secondsNeedleColor
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH*1.5f);
        paint2.setColor(hoursNeedleColor);
        float angle2=(6-hour)*30-minute*1/2-second*1/360;
        int rPadded2 = mCenterX - (int) (mWidth * 0.30f);
        int startX2=(int) (mCenterX + rPadded2 * Math.sin(Math.toRadians(angle2)));
        int startY2=(int) (mCenterX + rPadded2 * Math.cos(Math.toRadians(angle2)));
        canvas.drawLine(startX2, startY2, endX, endY, paint2);
        Log.d(TAG, "drawNeedles: "+angle2+minute);
        // - hoursNeedleColor
        Paint paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setStyle(Paint.Style.FILL_AND_STROKE);
        paint3.setStrokeCap(Paint.Cap.ROUND);
        paint3.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint3.setColor(minutesNeedleColor);
        float angle3=(30-minute)*6-second*1/60;
        int rPadded3 = mCenterX - (int) (mWidth * 0.25f);
        int startX3=(int) (mCenterX + rPadded3 * Math.sin(Math.toRadians(angle3)));
        int startY3=(int) (mCenterX + rPadded3 * Math.cos(Math.toRadians(angle3)));
        canvas.drawLine(startX3, startY3, endX, endY, paint3);
        // - minutesNeedleColor

    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint1.setColor(centerInnerColor);
        canvas.drawCircle(mCenterX,mCenterY,20,paint1);// - centerInnerColor

        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(6);
        paint2.setColor(centerOuterColor);
        canvas.drawCircle(mCenterX,mCenterY,20,paint2);// - centerOuterColor

    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}