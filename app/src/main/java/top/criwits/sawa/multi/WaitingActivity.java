package top.criwits.sawa.multi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.Multiple;
import top.criwits.sawa.network.WSService;

public class WaitingActivity extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // 什么也不做！
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waiting);
        Intent intent = getIntent();
        int roomID = intent.getIntExtra("top.criwits.sawa.NEW_ROOM_ID", 0);
        TextView label = (TextView) findViewById(R.id.waitingRoomID);
        label.setText(Integer.toString(roomID));
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String rawMsg = intent.getStringExtra("top.criwits.sawa.MESSAGE_RAW");
                JSONObject msg = JSON.parseObject(rawMsg);
                if (msg.getString("type").equals("room_ready")) {
                    sendResolutionInfo();
                }

                unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("top.criwits.sawa.MESSAGE");
        registerReceiver(receiver, filter);
    }

    private void sendResolutionInfo() {
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
                    // TODO: Start Game!
                    startGame();
                }

                unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("top.criwits.sawa.MESSAGE");
        registerReceiver(receiver, filter);
    }


    private void startGame() {
        Multiple.isHost = true;
        Intent intent = new Intent(this, MultiActivity.class);
        startActivity(intent);
    }
}