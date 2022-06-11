package top.criwits.sawa.multi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import top.criwits.sawa.R;

public class GameStatsticsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_statstics);
        Intent intent = getIntent();
        int thisScore = intent.getIntExtra("top.criwits.sawa.HERO_SCORE", 0);
        int teammateScore = intent.getIntExtra("top.criwits.sawa.FRIEND_SCORE", 0);
        TextView thisScoreView = findViewById(R.id.heroScore);
        TextView teammateScoreView = findViewById(R.id.friendScore);
        thisScoreView.setText(Integer.toString(thisScore));
        teammateScoreView.setText(Integer.toString(teammateScore));
    }
}