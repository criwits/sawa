package top.criwits.sawa.multi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.LoadConfig;
import top.criwits.sawa.config.Media;
import top.criwits.sawa.config.Multiple;
import top.criwits.sawa.media.ImageManager;
import top.criwits.sawa.media.MusicService;
import top.criwits.sawa.multi.GameView;
import top.criwits.sawa.network.WSService;

public class MultiActivity extends AppCompatActivity {

    private ServiceConnection conn;
    private Intent intent;
    GameView view;

    private void getScaleRatio() {
        // 计算缩放倍率
        Graphics.imageScalingFactor = (double)Graphics.screenWidth / (double) ImageManager.BG_IMG.getWidth();
        Graphics.pixelScalingFactor = (double)Graphics.screenWidth / (double)512;
        System.out.println("Screen scale ratio:" + Graphics.imageScalingFactor);
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Multiple.isMulti = true;
        enterFullScreenMode();

        // 发送分辨率信息！
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WSService.getClient().send("{\"type\": \"resolution\", \"width\": " + String.valueOf(dm.widthPixels) + ", \"height\": " + String.valueOf(dm.heightPixels) + "}");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String rawMsg = intent.getStringExtra("top.criwits.sawa.MESSAGE_RAW");
            JSONObject msg = JSON.parseObject(rawMsg);
            if (msg.getString("type").equals("game_start")) {
                Graphics.screenWidth = dm.widthPixels;
                Graphics.screenHeight = (int) (msg.getDouble("ratio") * dm.widthPixels);
                startGame();
            }

            unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("top.criwits.sawa.MESSAGE");
        registerReceiver(receiver, filter);
    }

    /**
     * 接到屏幕信息后启动游戏
     */
    private void startGame() {
        ImageManager.loadImages(getResources());
        getScaleRatio();
        ImageManager.reSizeAllBGs();

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

    private int lastX, lastY;
    /**
     * 触摸控制英雄机
     * @param event
     * @return
     */
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
            // 通知服务器，请求结束游戏
            WSService.getClient().send("{\"type\": \"game_end_request\", \"reason\": 1 }");
        }
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(conn);
    }

    public void gameOver(int reason, int thisScore, int teammateScore) {
        this.finish();
        Intent intent = new Intent(this, GameStatsticsActivity.class);
        intent.putExtra("top.criwits.sawa.HERO_SCORE", thisScore);
        intent.putExtra("top.criwits.sawa.FRIEND_SCORE", teammateScore);
        startActivity(intent);
    }
}