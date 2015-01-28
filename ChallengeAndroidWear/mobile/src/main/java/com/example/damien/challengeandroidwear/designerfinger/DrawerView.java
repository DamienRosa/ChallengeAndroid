package com.example.damien.challengeandroidwear.designerfinger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class DrawerView extends View {
    private static final String TAG = DrawerView.class.getSimpleName();
    //to draw path
    private Path drawPath;

    //to draw and canvas paint
    private Paint drawPaint;
    private Paint canvasPaint;

    //initial color
    private int drawPaintColor = Color.BLACK;

    //canvas
    private Canvas drawCanvas;

    //canvas bitmap
    private Bitmap canvasBitmap;

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configDrawing();
    }

    private void configDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(drawPaintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //size of view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    //draw view -> touch event
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    //register touches
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                drawPath.lineTo(x, y);
                break;
            }
            case MotionEvent.ACTION_UP: {
                drawPath.lineTo(x, y);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                drawPath.moveTo(x, y);
                break;
            }
            default:
                return false;
        }

        invalidate();
        return true;
    }

    // click on clear button
    public void clearDraw() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    //set color and size brush
    public void setConfigs(String color, String sizeBrush) {

        try {
            if (!color.equals("")) {
                int newColor = Color.parseColor(color);
                drawPaint.setColor(newColor);
                drawPaint.setShader(null);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "input error: color, formats: #RRGGBB or #AARRGGBB or red", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            if (!sizeBrush.equals("")) {
                float newSize = Float.valueOf(sizeBrush);
                float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
                drawPaint.setStrokeWidth(pixelAmount);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "input error: size", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getContext(), "Reconfigured", Toast.LENGTH_LONG).show();
    }
}