package top.criwits.sawa.ranking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.network.WSService;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        updateList();
    }

    private void updateList() {
        ListView listView = (ListView) findViewById(R.id.rankingListView);
        RankingDatabase db = RankingDatabase.getInstance(getApplicationContext());

        List<RankingEntry> rankingEntries = db.rankingDAO().queryAll();
        rankingEntries.removeIf(rankingEntry -> rankingEntry.difficulty != Difficulty.difficulty);
        rankingEntries.sort((t1, t2) -> t2.score - t1.score);

        RankingAdapter adapter = new RankingAdapter(RankingActivity.this, R.layout.ranking_item, rankingEntries);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RankingActivity.this);
                AlertDialog alert = builder.setTitle("Delete this ranking entry?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Nothing!
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RankingDatabase db = RankingDatabase.getInstance(getApplicationContext());
                                db.rankingDAO().delete(rankingEntries.get(i));
                                updateList();
                            }
                        }).create();             //创建AlertDialog对象
                alert.show();                    //显示对话框
            }
        });
    }
}