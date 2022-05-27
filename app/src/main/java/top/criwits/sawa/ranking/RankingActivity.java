package top.criwits.sawa.ranking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Difficulty;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

//        ListView listView = (ListView) findViewById(R.id.rankingListView);
//        RankingDatabase db = RankingDatabase.getInstance(getApplicationContext());
//
//        List<RankingEntry> rankingEntries = db.rankingDAO().queryAll();
////        rankingEntries.removeIf(rankingEntry -> rankingEntry.difficulty != Difficulty.difficulty);
////        List<String> str = new LinkedList<>();
////        for (int i = 0; i < rankingEntries.size(); i++) {
////            str.add(rankingEntries.get(i).playerName + rankingEntries.get(i).score);
////        }
////
////        ArrayAdapter<String> adapter=new ArrayAdapter<>(RankingActivity.this,android.R.layout.simple_list_item_1, str);
//
////        listView.setAdapter(adapter);
    }
}