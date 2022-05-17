package top.criwits.sawa.solo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.LoadConfig;
import top.criwits.sawa.config.Media;
import top.criwits.sawa.media.ImageManager;
import top.criwits.sawa.media.MusicService;
import top.criwits.sawa.media.SoundHelper;

public class SoloActivity extends AppCompatActivity {

    private ServiceConnection conn;
    private Intent intent;
    GameView view;

    /**
     * 获得屏幕尺寸，然后写入这个 Activity 的
     * screenHeight 和 screenWidth 变量
     */
    private void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Graphics.screenHeight = dm.heightPixels;
        Graphics.screenWidth = dm.widthPixels;
    }

    private void getScaleRatio() {
        // 计算缩放倍率
        Graphics.scalingFactor = (double)Graphics.screenWidth / (double)ImageManager.BG_IMG.getWidth();
        System.out.println("Screen scale ratio:" + Graphics.scalingFactor);
    }

    /**
     * 进入粘性沉浸模式
     * 这种模式下，状态栏、导航栏全部不可见
     * 参考：https://developer.android.google.cn/training/system-ui/immersive
     */
    private void enterFullScreenMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // 让内容可以被状态栏覆盖，
                        // 这样就不会因为状态栏、导航栏等的出现和消失造成问题了
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // 隐藏状态栏、导航栏
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     * 加载图片资源
     */
    private void loadImages() {
        ImageManager.loadImages(getResources());
    }

    /**
     * onCreate() 会在这个 Activity 启动时被调用。
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadImages();
        enterFullScreenMode();
        getScreenSize();
        getScaleRatio();

        // 获取难度信息
        Intent intent = getIntent();
        switch(intent.getIntExtra("top.criwits.sawa.DIFFICULTY_INDEX", 0)) {
            case 0:
                LoadConfig.loadEasyMode();
                break;
            case 1:
                LoadConfig.loadModerateMode();
                break;
            case 2:
                LoadConfig.loadHardMode();
                break;
        }

        view = new GameView(this, Graphics.screenHeight, Graphics.screenWidth);
        setContentView(view);

        // 绑定音乐服务
        System.out.println(Media.music);
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) { }
            @Override
            public void onServiceDisconnected(ComponentName componentName) { }
        };
        intent = new Intent(this, MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 触摸控制英雄机
     * @param event
     * @return
     */

    private int lastX, lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = (int) event.getX();
            lastY = (int) event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int currentX = (int) event.getX(), currentY = (int) event.getY();
            view.moveHeroAircraft(currentX - lastX, currentY - lastY);
            lastX = currentX;
            lastY = currentY;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
        }
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(conn);
    }
}