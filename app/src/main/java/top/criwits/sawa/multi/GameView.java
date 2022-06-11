package top.criwits.sawa.multi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import top.criwits.sawa.R;
import top.criwits.sawa.model.aircraft.FriendAircraft;
import top.criwits.sawa.model.aircraft.HeroAircraft;
import top.criwits.sawa.model.basic.AbstractFlyingObject;
import top.criwits.sawa.config.GameClock;
import top.criwits.sawa.config.Kinematics;
import top.criwits.sawa.config.Media;

/**
 * GameView
 * 是游戏的主 View
 */
public class GameView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {

    public GameLogic game;

    private int backgroundOffset = 0;
    private int cycleTime = 0;
    private int mspf = 0;

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
        /*
         * 因为现在的手机都是全面屏，
         * 需要 3 张图来拼背景。
         */
        canvas.drawBitmap(bgimg, 0, backgroundOffset - bgimg.getHeight(), paint);
        canvas.drawBitmap(bgimg, 0, backgroundOffset, paint);
        canvas.drawBitmap(bgimg, 0, backgroundOffset + bgimg.getHeight(), paint);
        backgroundOffset += Kinematics.getRealPixel(Kinematics.backgroundShiftPerFrame);
        if (backgroundOffset >= bgimg.getHeight()) {
            backgroundOffset = 0;
        }
    }

    /**
     * 画实体
     * @param flyingObject 要画的实体
     * @param canvas
     * @param paint
     */
    private void drawFlyingObject(AbstractFlyingObject flyingObject, @NonNull Canvas canvas, Paint paint) {
        Rect srcRect = new Rect(0, 0, flyingObject.getImage().getWidth(), flyingObject.getImage().getHeight());
        Rect dstRect = new Rect(flyingObject.getLocationX() - flyingObject.getWidth() / 2,
                flyingObject.getLocationY() - flyingObject.getHeight() / 2,
                flyingObject.getLocationX() + flyingObject.getWidth() / 2,
                flyingObject.getLocationY() + flyingObject.getHeight() / 2);
        canvas.drawBitmap(flyingObject.getImage(), srcRect, dstRect, paint);
    }


    /**
     * 每帧的绘图函数
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void draw(){
        canvas = sh.lockHardwareCanvas();
        if(sh == null || canvas == null){
            return;
        }

        // 画背景
        drawBackground(Media.backgroundImage, backgroundOffset, canvas, paint);

        // 依次画四种实体
        for (int i = 0; i < game.getEnemyBulletsList().size(); i++) {
            drawFlyingObject(game.getEnemyBulletsList().get(i), canvas, paint);
        }
        for (int i = 0; i < game.getHeroBulletsList().size(); i++) {
            drawFlyingObject(game.getHeroBulletsList().get(i), canvas, paint);
        }
        for (int i = 0; i < game.getFriendBullets().size(); i++) {
            drawFlyingObject(game.getFriendBullets().get(i), canvas, paint);
        }
        for (int i = 0; i < game.getPropsList().size(); i++) {
            drawFlyingObject(game.getPropsList().get(i), canvas, paint);
        }
        for (int i = 0; i < game.getEnemyAircraftList().size(); i++) {
            drawFlyingObject(game.getEnemyAircraftList().get(i), canvas, paint);
        }

        // 画英雄机
        drawFlyingObject(FriendAircraft.getInstance(), canvas, paint);
        drawFlyingObject(HeroAircraft.getInstance(), canvas, paint);

        // 分数
        Paint textPaint = new Paint();
        textPaint.setColor(Color.RED);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/IBMPlexSans-Bold.ttf");
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.scoreFontSize));
        canvas.drawText("SCORE: " + String.valueOf(game.getScore()), Kinematics.getRealPixel(20), Kinematics.getRealPixel(75), textPaint);
        canvas.drawText("HP: " + String.valueOf(HeroAircraft.getInstance().getHp()), Kinematics.getRealPixel(20), Kinematics.getRealPixel(110), textPaint);
        textPaint.setColor(Color.GRAY);
        canvas.drawText("MSPF: " + String.valueOf(mspf) + " ms", Kinematics.getRealPixel(20), Kinematics.getRealPixel(145), textPaint);
        sh.unlockCanvasAndPost(canvas);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        game = new GameLogic();
        // 游戏主循环
        while (canDraw){
            long startTime = System.currentTimeMillis();
            // 执行游戏逻辑
            if (timeCountAndNewCycleJudge()) {
                game.doAtEveryCycle();
            }
            game.doAtEveryTick();
            // 绘图
            synchronized (sh){
                draw();
            }
            long endTime = System.currentTimeMillis();
            if (game.getGameOver()) {
                canDraw = false;
            }
            try {
                Thread.sleep(Math.max(GameClock.timeInterval - (endTime - startTime), 0));
                mspf = (endTime - startTime) > GameClock.timeInterval ? (int)(endTime - startTime) :
                        GameClock.timeInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Game Over
        MultiActivity activity = (MultiActivity) getContext();
        activity.gameOver(0, game.getScore(), game.getFriendScore());
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

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += GameClock.timeInterval;
        if (cycleTime >= GameClock.cycleDuration && cycleTime - GameClock.timeInterval < cycleTime) {
            cycleTime %= GameClock.cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    public void moveHeroAircraft(int deltaX, int deltaY) {
        HeroAircraft.getInstance().move(deltaX, deltaY);
    }
}