package com.example.photoannotation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class DrawView extends View {
    private Bitmap bitmap;
    private Paint paint;
    private List<Stroke> _allStrokes;
    private SparseArray<Stroke> _activeStrokes;
    private float strokeWidth = 5;
    private int mode = 0;
    private Canvas mCanvas;
    private boolean clearing = false;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _allStrokes = new ArrayList<Stroke>();
        _activeStrokes = new SparseArray<Stroke>();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bitmap = Bitmap.createBitmap(960, 1500, Bitmap.Config.ARGB_8888);
        bitmap = bitmap.copy(ARGB_8888, true);
        mCanvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        if (_allStrokes != null) {
            for (Stroke stroke: _allStrokes) {
                if (stroke != null) {
                    Path path = stroke.getPath();
                    Paint painter = stroke.getPaint();
                    if ((path != null) && (painter != null)) {
                        mCanvas.drawPath(path, painter);
                    }
                }
            }
        }
        if (clearing) {
            mCanvas.drawColor(Color.TRANSPARENT);
            clearing = false;
        }
    }

    private void pointDown(int x, int y, int id) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        if (mode == 0) {
            paint.setColor(Color.BLACK);
            paint.setXfermode(null);
        } else {
            paint.setColor(Color.TRANSPARENT);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        Point pt = new Point(x, y);
        Stroke stroke = new Stroke(paint);
        stroke.addPoint(pt);
        _activeStrokes.put(id, stroke);
        _allStrokes.add(stroke);
    }

    private void pointMove(int x, int y, int id) {
        Stroke stroke = _activeStrokes.get(id);
        if (stroke != null) {
            Point pt = new Point(x, y);
            stroke.addPoint(pt);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        final int pointerCount = event.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                pointDown((int)event.getX(), (int)event.getY(), event.getPointerId(0));
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int pc = 0; pc < pointerCount; pc++) {
                    pointMove((int) event.getX(pc), (int) event.getY(pc), event.getPointerId(pc));
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                for (int pc = 0; pc < pointerCount; pc++) {
                    pointDown((int)event.getX(pc), (int)event.getY(pc), event.getPointerId(pc));
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                break;
            }
        }
        invalidate();
        return true;
    }

    public void smallBrush() {
        strokeWidth = 5;
    }

    public void bigBrush() {
        strokeWidth = 20;
    }

    public void draw() {
        mode = 0;
    }

    public void erase() {
        mode = 1;
    }

    public void clearStrokes() {
        clearing = true;
        _allStrokes = new ArrayList<Stroke>();
        _activeStrokes = new SparseArray<Stroke>();
        invalidate();
    }

    public class Stroke {
        private Path _path;
        private Paint _paint;

        public Stroke (Paint paint) {
            _paint = paint;
        }

        public Path getPath() {
            return _path;
        }

        public Paint getPaint() {
            return _paint;
        }

        public void addPoint(Point pt) {
            if (_path == null) {
                _path = new Path();
                _path.moveTo(pt.x, pt.y);
            } else {
                _path.lineTo(pt.x, pt.y);
            }
        }
    }
}
