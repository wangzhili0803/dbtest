package com.vince.dbtest.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2015/12/15.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private MyAction action;

    public MySurfaceView(Context context) {
        super(context);
        holder = this.getHolder();
        holder.addCallback(this);
    }

    class MyAction implements Runnable {
        private SurfaceHolder holder;
        public boolean isRun;

        public MyAction(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }

        @Override
        public void run() {
            int i = 0;
            Canvas canvas = null;
            while (isRun) {
                try {
                    synchronized (holder) {
                        canvas = holder.lockCanvas();
                        canvas.drawColor(Color.BLACK);
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setAntiAlias(true);
                        paint.setStyle(Paint.Style.FILL);
                        paint.setTextSize(30);
                        canvas.drawRect(10, 10, 100, 100, paint);
                        canvas.drawText("减肥是开放接口来说！" + (i++), 10, 150, paint);
                        Thread.sleep(200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }

            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        action = new MyAction(holder);
        new Thread(action).start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        action.isRun = false;
    }
}
