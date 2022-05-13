package top.criwits.sawa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    SimpleSurfaceView view;

    private int screenHeight, screenWidth;

    /**
     * 获得屏幕尺寸，然后写入这个 Activity 的
     * screenHeight 和 screenWidth 变量
     */
    private void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        System.out.println(screenHeight +","+ screenWidth);
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
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
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
        enterFullScreenMode();
        getScreenSize();
        loadImages();
        view = new SimpleSurfaceView(this, screenHeight, screenWidth);
        setContentView(view);
    }



}