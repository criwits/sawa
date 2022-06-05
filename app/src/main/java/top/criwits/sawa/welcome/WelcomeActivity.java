package top.criwits.sawa.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Media;
import top.criwits.sawa.multi.RoomSelectActivity;
import top.criwits.sawa.network.WSService;
import top.criwits.sawa.solo.SoloActivity;

public class WelcomeActivity extends AppCompatActivity {

    private int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_welcome, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.soloEasyMode:
                if (checked) {
                    difficulty = 0;
                    System.out.println("Easy");
                }
                break;
            case R.id.soloModerateMode:
                if (checked) {
                    difficulty = 1;
                    System.out.println("Moderate");
                }
                break;
            case R.id.soloHardMode:
                if (checked) {
                    difficulty = 2;
                    System.out.println("Hard");
                }
                break;
        }
    }

    public void audioChange(View view) {
        Media.music = ((Switch) view).isChecked();
    }


    public void startSoloGame(View view) {
        Intent intent = new Intent(this, SoloActivity.class);
        intent.putExtra("top.criwits.sawa.DIFFICULTY_INDEX", difficulty);
        startActivity(intent);
    }

    public void startMultiGame(View view) {
        // 获取服务器地址、用户名、密码
        EditText address = (EditText) findViewById(R.id.multiServerAddress);


        Intent intent = new Intent(this, WSService.class);
        intent.putExtra("top.criwits.sawa.MULTIADDR", address.getText().toString());
        startService(intent);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("top.criwits.sawa.CONNSTATUS", false)) {
                    userQuery();
                }
                unregisterReceiver(this);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("top.criwits.sawa.CONNECTION");
        registerReceiver(receiver, filter);

    }

    private void userQuery() {
        EditText username = (EditText) findViewById(R.id.multiUserName);
        EditText password = (EditText) findViewById(R.id.multiPassword);
        WSService.getClient().send("{ \"type\": \"user_query\", " +
                "\"username\": \"" + username.getText().toString() + "\",\n" +
                "\"password\": \"" + password.getText().toString() + "\" }");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String rawMsg = intent.getStringExtra("top.criwits.sawa.MESSAGE_RAW");
                JSONObject msg = JSON.parseObject(rawMsg);
                if (msg.getString("type").equals("user_query_response")) {
                    if (msg.getInteger("uid") != -1) {
                        startMultiActivity();
                    }
                }

                unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("top.criwits.sawa.MESSAGE");
        registerReceiver(receiver, filter);
    }

    private void startMultiActivity() {
        Intent intent = new Intent(this, RoomSelectActivity.class);
        startActivity(intent);
    }

}