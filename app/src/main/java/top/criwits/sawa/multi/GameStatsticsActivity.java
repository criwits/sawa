package top.criwits.sawa.multi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.network.WSService;

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

        WSService.getClient().send("{\"type\": \"get_rankings\"}");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String rawMsg = intent.getStringExtra("top.criwits.sawa.MESSAGE_RAW");
                JSONObject msg = JSON.parseObject(rawMsg);

                List<RankingEntry> rankingEntries = new LinkedList<>();
                JSONArray roomArray = msg.getJSONArray("rankings");
                for (int i = 0; i < roomArray.size(); i++) {
                    JSONObject object = roomArray.getJSONObject(i);
                    rankingEntries.add(new RankingEntry(
                            object.getString("username"),
                            object.getInteger("score"),
                            object.getInteger("enroll_date"),
                            object.getInteger("difficulty")
                    ));
                }
                System.out.println(roomArray.size());

                rankingEntries.removeIf(rankingEntry -> rankingEntry.getDifficulty() != Difficulty.difficulty);
                rankingEntries.sort((t1, t2) -> t2.getScore() - t1.getScore());

                RankingAdapter adapter = new RankingAdapter(GameStatsticsActivity.this, R.layout.ranking_item, rankingEntries);
                ListView view = (ListView) findViewById(R.id.multiRanking);
                view.setAdapter(adapter);


                unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("top.criwits.sawa.MESSAGE");
        registerReceiver(receiver, filter);
    }
}