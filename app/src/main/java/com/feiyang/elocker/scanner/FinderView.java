package com.feiyang.elocker.scanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import com.feiyang.elocker.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.ArrayList;
import java.util.List;

/*自定义扫描框*/
public class FinderView extends ViewfinderView {
    public int laserLinePosition = 0;
    public float[] position = new float[]{0f, 0.5f, 1f};
    public int[] colors = new int[]{0x00ffffff, 0xffffffff, 0x00ffffff};
    public LinearGradient linearGradient;

    public FinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        Rect frame = framingRect;
        Rect previewFrame = previewFramingRect;
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        /*绘制4个角*/
        paint.setColor(getResources().getColor(R.color.zxing_scanner_corner_color, null));
        canvas.drawRect(frame.left, frame.top, frame.left + 70, frame.top + 10, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + 10, frame.top + 70, paint);

        canvas.drawRect(frame.right - 70, frame.top, frame.right, frame.top + 10, paint);
        canvas.drawRect(frame.right - 10, frame.top, frame.right, frame.top + 70, paint);

        canvas.drawRect(frame.left, frame.bottom - 10, frame.left + 70, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - 70, frame.left + 10, frame.bottom, paint);

        canvas.drawRect(frame.right - 70, frame.bottom - 10, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - 10, frame.bottom - 70, frame.right, frame.bottom, paint);

        /*绘制扫描框外部的半透明阴影部分*/
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            /*在扫描框上绘制不透明扫描结果框*/
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            laserLinePosition = laserLinePosition > frame.height() ? 0 : laserLinePosition + 5;
            linearGradient = new LinearGradient(frame.left + 1, frame.top + laserLinePosition, frame.right - 1, frame.top + 10 + laserLinePosition, colors, position, Shader.TileMode.CLAMP);
            paint.setColor(getResources().getColor(R.color.zxing_laser_color, null));
            canvas.drawRect(frame.left + 1, frame.top + laserLinePosition, frame.right - 1, frame.top + 10 + laserLinePosition, paint);
            paint.setShader(null);

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;

            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX), frameTop + (int) (point.getY() * scaleY), POINT_SIZE, paint);
                }
            }

            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX), frameTop + (int) (point.getY() * scaleY), radius, paint);
                }
            }
            postInvalidateDelayed(16, frame.left, frame.top, frame.right, frame.bottom);
        }
    }
}
