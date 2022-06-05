package top.criwits.sawa.multi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import top.criwits.sawa.R;

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
    }
}