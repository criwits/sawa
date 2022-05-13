package top.criwits.sawa;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

/**
 * Simple Surface View
 */
public class SimpleSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {

    int screenWidth = 480, screenHeight = 800;
    boolean canDraw = false; //控制绘画线程的标志位
    private SurfaceHolder sh;
    private Canvas canvas;  //绘图的画布
    private Paint paint;

    public SimpleSurfaceView(Context context, int screenHeight, int screenWidth) {
        super(context);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        canDraw = true;
        paint = new Paint();
        sh = this.getHolder();
        sh.addCallback(this);
        this.setFocusable(true);
    }

    public void draw(){
        canvas = sh.lockCanvas();
        if(sh == null || canvas == null){
            return;
        }

        canvas.drawBitmap(ImageManager.BG_IMG, 0, 0, paint);

        sh.unlockCanvasAndPost(canvas);
    }
    @Override
    public void run() {
        //设置一个循环来绘制，通过标志位来控制开启绘制还是停止
        while (canDraw){
            synchronized (sh){
                draw();
            }
            try {
                Thread.sleep(200);
            }catch (Exception e){}
        }
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        new Thread(this).start();
    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        canDraw = false;
    }
}