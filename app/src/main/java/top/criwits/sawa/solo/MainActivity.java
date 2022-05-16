package top.criwits.sawa.solo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.utils.ImageManager;

public class MainActivity extends AppCompatActivity {
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
        view = new GameView(this, Graphics.screenHeight, Graphics.screenWidth);
        setContentView(view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (view.checkHeroMovement((int)event.getX(), (int)event.getY())) {
                view.setHeroLocation((int)event.getX(), (int)event.getY());
            }
        }
        return  true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
        }
        return true;
    }



}