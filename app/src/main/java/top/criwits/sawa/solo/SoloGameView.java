package top.criwits.sawa.solo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import top.criwits.sawa.R;
import top.criwits.sawa.aircraft.AbstractAircraft;
import top.criwits.sawa.aircraft.AircraftFactory;
import top.criwits.sawa.aircraft.EliteEnemy;
import top.criwits.sawa.aircraft.EliteEnemyFactory;
import top.criwits.sawa.aircraft.HeroAircraft;
import top.criwits.sawa.aircraft.MobEnemyFactory;
import top.criwits.sawa.basic.AbstractFlyingObject;
import top.criwits.sawa.bullet.AbstractBullet;
import top.criwits.sawa.config.AircraftHP;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.GameClock;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.Kinematics;
import top.criwits.sawa.config.Media;
import top.criwits.sawa.config.Probability;
import top.criwits.sawa.prop.AbstractProp;
import top.criwits.sawa.utils.ImageManager;
import top.criwits.sawa.utils.RandomGenerator;

/**
 * GameView
 * 是游戏的主 View
 */
public class SoloGameView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {

    private int backgroundOffset = 0;
    private int cycleTime = 0;
    private boolean gameOverFlag = false;
    private int score = 0;
    private int time = 0;

    /* 存放各个实体的列表 */
    private final List<AbstractAircraft> enemyAircraft;
    private final List<AbstractBullet> heroBullets;
    private final List<AbstractBullet> enemyBullets;
    private final List<AbstractProp> props;


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
    public SoloGameView(Context context, int screenHeight, int screenWidth) {
        super(context);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        canDraw = true;
        paint = new Paint();
        sh = this.getHolder();
        sh.addCallback(this);
        this.setFocusable(true);

        // 初始化各个 List
        enemyAircraft = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();
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

//        Rect bgSrcRect = new Rect(0, 0, bgimg.getWidth(), bgimg.getHeight());
//        Rect bgMidRect = new Rect(0, bgOffset, Graphics.screenWidth,
//                (int)(bgimg.getHeight() * Graphics.scalingFactor) + bgOffset);
//        Rect bgUpperRect = new Rect(0, bgOffset - (int)(bgimg.getHeight() * Graphics.scalingFactor),
//                Graphics.screenWidth, bgOffset);
//        Rect bgBottomRect = new Rect(0, (int)(bgimg.getHeight() * Graphics.scalingFactor) + bgOffset,
//                Graphics.screenWidth, 2 * (int)(bgimg.getHeight() * Graphics.scalingFactor) + bgOffset);
//        canvas.drawBitmap(bgimg, bgSrcRect, bgMidRect, paint);
//        canvas.drawBitmap(bgimg, bgSrcRect, bgUpperRect, paint);
//        canvas.drawBitmap(bgimg, bgSrcRect, bgBottomRect, paint);

        canvas.drawBitmap(bgimg, 0, backgroundOffset - bgimg.getHeight(), paint);
        canvas.drawBitmap(bgimg, 0, backgroundOffset, paint);
        canvas.drawBitmap(bgimg, 0, backgroundOffset + bgimg.getHeight(), paint);
//        paint.setColor(Color.BLACK);
//        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        backgroundOffset += Kinematics.backgroundShiftPerFrame;
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
    public void draw(){
        canvas = sh.lockCanvas();
        if(sh == null || canvas == null){
            return;
        }

        // 画背景
        drawBackground(Media.backgroudImage, backgroundOffset, canvas, paint);

        // 依次画四种实体
        for (int i = 0; i < enemyBullets.size(); i++) {
            drawFlyingObject(enemyBullets.get(i), canvas, paint);
        }
        for (int i = 0; i < heroBullets.size(); i++) {
            drawFlyingObject(heroBullets.get(i), canvas, paint);
        }
        for (int i = 0; i < props.size(); i++) {
            drawFlyingObject(props.get(i), canvas, paint);
        }
        for (int i = 0; i < enemyAircraft.size(); i++) {
            drawFlyingObject(enemyAircraft.get(i), canvas, paint);
        }

        // 画精英机
        drawFlyingObject(HeroAircraft.getInstance(), canvas, paint);

        // 分数
        Paint textPaint = new Paint();
        textPaint.setColor(Color.RED);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/IBMPlexSans-Bold.ttf");
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.scoreFontSize));
        canvas.drawText("SCORE: " + String.valueOf(score), 40,150, textPaint);
        canvas.drawText("LIFE: " + String.valueOf(HeroAircraft.getInstance().getHp()), 40,220, textPaint);

        sh.unlockCanvasAndPost(canvas);
    }

    
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        // 精英机加载
        HeroAircraft.loadInstance(
                Graphics.screenWidth / 2,
                Graphics.screenHeight - ImageManager.HERO_IMG.getHeight() ,
                0, 0, AircraftHP.heroAircraftHP);

        // 游戏主循环
        while (canDraw){

            long startTime = System.currentTimeMillis();

            if (timeCountAndNewCycleJudge()) {
                // Spawn of enemies if can
                if (enemyAircraft.size() < Difficulty.enemyMaxNumber) {
                    AircraftFactory newAircraftFactory;
                    int speedX;
                    int hp;
                    // Decide which type of enemy should be spawned
                    if (Math.random() < Probability.eliteProbability) {
                        newAircraftFactory = new EliteEnemyFactory();
                        speedX = RandomGenerator.nonZeroGenerator(Kinematics.enemySpeedX);
                        hp = AircraftHP.eliteEnemyHP;
                    } else {
                        newAircraftFactory = new MobEnemyFactory();
                        speedX = 0;
                        hp = AircraftHP.mobEnemyHP;
                    }
                    enemyAircraft.add(newAircraftFactory.createAircraft(
                            (int) (Math.random() * (Graphics.screenWidth - ImageManager.MOB_IMG.getWidth())),
                            (int) (Math.random() * Graphics.screenHeight * 0.2),
                            speedX,
                            Kinematics.enemySpeedY,
                            hp
                    ));
                }
                // Shoot
                shootAction();
            }
            // Increase difficulty
            difficultyIncrease();
            // Boss Generation
            bossGenerateAction(score, enemyAircraft);
            // Bullet move
            bulletsMoveAction();
            // Aircraft move
            aircraftsMoveAction();
            // props move
            propsMoveAction();
            // Crash check
            crashCheckAction();
            // Post process
            postProcessAction();

            // 绘图
            synchronized (sh){
                draw();
            }

            long endTime = System.currentTimeMillis();

            try {
                Thread.sleep(Math.max(GameClock.timeInterval - (endTime - startTime), 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += GameClock.timeInterval;
        if (cycleTime >= GameClock.cycleDuration && cycleTime - GameClock.timeInterval < cycleTime) {
            cycleTime %= GameClock.cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    protected void bossGenerateAction(int score, List<AbstractAircraft> enemyAircrafts) {};

    private void shootAction() {
        // Enemies
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        // Hero
        heroBullets.addAll(HeroAircraft.getInstance().shoot());
    }

    private void bulletsMoveAction() {
        for (AbstractBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (AbstractBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }


    /** Hit box detection */
    private void crashCheckAction() {
        // Enemy bullets
        for (AbstractBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if(HeroAircraft.getInstance().crash(bullet)) {
                HeroAircraft.getInstance().decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // Player bullets
        for (AbstractBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircraft) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // PlaySound.playBulletHitSound();
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                }

            }
        }

        // Props spawn
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            if (enemyAircraft.notValid()) {
                // Add score!
                score += enemyAircraft.addScore();
                props.addAll(enemyAircraft.generateProp());
            }
        }

        // Enemy and Hero crash
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            if (enemyAircraft.crash(HeroAircraft.getInstance()) || HeroAircraft.getInstance().crash(enemyAircraft)) {
                enemyAircraft.vanish();
                HeroAircraft.getInstance().decreaseHp(Integer.MAX_VALUE);
            }
        }

        for (AbstractProp prop: props) {
            if (HeroAircraft.getInstance().crash(prop)) {
                score += prop.action(HeroAircraft.getInstance(), enemyAircraft, enemyBullets);
                prop.vanish();
            }
        }

    }

    /**
     * Post process
     *   - Remove invalid enemies
     *   - Remove invalid bullets
     *   - Remove invalid & used props
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircraft.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    protected void difficultyIncrease() {}

    /**
     * 检查按下的位置是否可以拖动英雄机
     * @param locationX
     * @param locationY
     * @return
     */
    public boolean checkHeroMovement(int locationX, int locationY) {
        if ((HeroAircraft.getInstance().getLocationX() - HeroAircraft.getInstance().getWidth() / 2
                <= locationX && locationX
                <= HeroAircraft.getInstance().getLocationX() + HeroAircraft.getInstance().getWidth() / 2) &&
            (HeroAircraft.getInstance().getLocationY() - HeroAircraft.getInstance().getHeight() / 2
                    <= locationY && locationY
                    <= HeroAircraft.getInstance().getLocationY() + HeroAircraft.getInstance().getHeight() / 2)
        ) {
            return true;
        } else {
            return false;
        }
    }

    public void setHeroLocation(int locationX, int locationY) {
        HeroAircraft.getInstance().setLocation((double)locationX, (double) locationY);
    }
}