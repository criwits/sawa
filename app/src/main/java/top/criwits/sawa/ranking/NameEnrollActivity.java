package top.criwits.sawa.ranking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import top.criwits.sawa.R;

public class NameEnrollActivity extends AppCompatActivity {

    int score = 0;
    int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_enroll);
        Intent intent = getIntent();
        score = intent.getIntExtra("top.criwits.sawa.SCORE", 0);
        difficulty = intent.getIntExtra("top.criwits.sawa.DIFFICULTY", 0);
        TextView scoreView = findViewById(R.id.scoreTextView);
        scoreView.setText(Integer.toString(score));
    }

    private void enroll(String name) {
        RankingDatabase db = RankingDatabase.getInstance(getApplicationContext());
        db.rankingDAO().insertEntry(
                new RankingEntry(0, "Hans", 100, 0)
        );
    }

    public void enrollOnClick(View view) {
        EditText editText = (EditText) findViewById(R.id.rankingUserName);
        enroll(editText.toString());
        cancelOnClick(view);
    }

    public void cancelOnClick(View view) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }
}