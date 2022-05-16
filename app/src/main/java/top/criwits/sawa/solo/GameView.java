package top.criwits.sawa.solo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.utils.ImageManager;

/**
 * GameView
 * 是游戏的主 View
 */
public class GameView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {

    int screenWidth, screenHeight;
    boolean canDraw = false; //控制绘画线程的标志位
    private SurfaceHolder sh;
    private Canvas canvas;  //绘图的画布
    private Paint paint;

    /**
     * View 初始化
     * @param context
     * @param screenHeight
     * @param screenWidth
     */
    public GameView(Context context, int screenHeight, int screenWidth) {
        super(context);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        canDraw = true;
        paint = new Paint();
        sh = this.getHolder();
        sh.addCallback(this);
        this.setFocusable(true);
    }

    /**
     * 画背景
     * @param bgimg 背景的 Bitmap
     * @param bgOffset 背景偏移量 [0, Graphics.screenHeight]
     * @param canvas
     * @param paint
     */
    private void drawBackground(@NonNull Bitmap bgimg, int bgOffset, @NonNull Canvas canvas, Paint paint) {
        /**
         * 因为现在的手机都是全面屏，
         * 需要 3 张图来拼背景。
         */
        System.out.println(bgimg.getHeight() + ", " + bgimg.getWidth());
        Rect bgSrcRect = new Rect(0, 0, bgimg.getWidth(), bgimg.getHeight());
        Rect bgMidRect = new Rect(0, bgOffset, Graphics.screenWidth,
                (int)(bgimg.getHeight() * Graphics.scalingFactor) + bgOffset);
        Rect bgUpperRect = new Rect(0, bgOffset - (int)(bgimg.getHeight() * Graphics.scalingFactor),
                Graphics.screenWidth, bgOffset);
        Rect bgBottomRect = new Rect(0, (int)(bgimg.getHeight() * Graphics.scalingFactor) + bgOffset,
                Graphics.screenWidth, 2 * (int)(bgimg.getHeight() * Graphics.scalingFactor) + bgOffset);
        canvas.drawBitmap(bgimg, bgSrcRect, bgMidRect, paint);
        canvas.drawBitmap(bgimg, bgSrcRect, bgUpperRect, paint);
        canvas.drawBitmap(bgimg, bgSrcRect, bgBottomRect, paint);
    }

    /**
     * 每帧的绘图函数
     */
    public void draw(int offset){
        canvas = sh.lockCanvas();
        if(sh == null || canvas == null){
            return;
        }

        drawBackground(ImageManager.BG_IMG, offset, canvas, paint);

        sh.unlockCanvasAndPost(canvas);
    }
    @Override
    public void run() {
        int offset = 0;
        //设置一个循环来绘制，通过标志位来控制开启绘制还是停止
        while (canDraw){
            synchronized (sh){
                draw(offset += 10);
            }
            try {
                Thread.sleep(1);
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