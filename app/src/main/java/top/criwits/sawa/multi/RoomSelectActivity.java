package top.criwits.sawa.multi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.LoadConfig;
import top.criwits.sawa.config.Multiple;
import top.criwits.sawa.network.WSService;

public class RoomSelectActivity extends AppCompatActivity {

    int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_select);
        updateRoomList();
    }

    private void updateRoomList() {
        WSService.getClient().send("{\"type\": \"room_info\"}");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String rawMsg = intent.getStringExtra("top.criwits.sawa.MESSAGE_RAW");
                JSONObject msg = JSON.parseObject(rawMsg);
                if (msg.getString("type").equals("room_info_response")) {
                    // reference!!!!!!!!
                    List<RoomEntry> roomEntries = new LinkedList<>();
                    JSONArray roomArray = msg.getJSONArray("rooms");
                    for (int i = 0; i < roomArray.size(); i++) {
                        JSONObject object = roomArray.getJSONObject(i);
                        roomEntries.add(new RoomEntry(
                                object.getInteger("room_id"),
                                object.getInteger("difficulty")
                        ));
                    }
                    System.out.println(roomArray.size());

                    showRooms(roomEntries);
                }

                unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("top.criwits.sawa.MESSAGE");
        registerReceiver(receiver, filter);
    }

    private void showRooms(List<RoomEntry> list) {
        RoomAdapter adapter = new RoomAdapter(RoomSelectActivity.this, R.layout.room_item, list);
        ListView listView = (ListView) findViewById(R.id.roomList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(list.get(i).getRoomID());
                // 加入房间
                WSService.getClient().send("{\"type\": \"join_room\", \"room_id\":" + String.valueOf(list.get(i).getRoomID()) + " }");
                BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String rawMsg = intent.getStringExtra("top.criwits.sawa.MESSAGE_RAW");
                        JSONObject msg = JSON.parseObject(rawMsg);
                        if (msg.getString("type").equals("join_room_response") &&
                                msg.getBooleanValue("success")) {
                            Difficulty.difficulty = list.get(i).getDifficulty();
                            startGameActivity();
                        }

                        unregisterReceiver(this);
                    }
                };

                IntentFilter filter = new IntentFilter();
                filter.addAction("top.criwits.sawa.MESSAGE");
                registerReceiver(receiver, filter);
            }
        });
    }


    private void startGameActivity() {
        Multiple.isHost = false;
        Intent intent = new Intent(this, MultiActivity.class);
        intent.putExtra("top.criwits.sawa.DIFFICULTY_INDEX", Difficulty.difficulty);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_room_select, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.multiEasyMode:
                if (checked) {
                    difficulty = 0;
                }
                break;
            case R.id.multiModerateMode:
                if (checked) {
                    difficulty = 1;
                }
                break;
            case R.id.multiHardMode:
                if (checked) {
                    difficulty = 2;
                }
                break;
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.reload:
                updateRoomList();
                return true;
            case R.id.addRoom:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose difficulty");
                View v = getLayoutInflater().inflate(R.layout.dialog_create_room, null);
                builder.setView(v);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WSService.getClient().send("{\n" +
                                "  \"type\": \"create_room\",\n" +
                                "  \"difficulty\": " + String.valueOf(difficulty) + "\n" +
                                "}");
                        BroadcastReceiver receiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String rawMsg = intent.getStringExtra("top.criwits.sawa.MESSAGE_RAW");
                                JSONObject msg = JSON.parseObject(rawMsg);
                                if (msg.getString("type").equals("create_room_response")) {
                                    int roomID = msg.getInteger("room_id");
                                    Difficulty.difficulty = difficulty;
                                    startWaiting(roomID);
                                }

                                unregisterReceiver(this);
                            }
                        };

                        IntentFilter filter = new IntentFilter();
                        filter.addAction("top.criwits.sawa.MESSAGE");
                        registerReceiver(receiver, filter);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 啥也不干！
                    }
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startWaiting(int roomID) {
        // 等待画面
        Intent intent = new Intent(RoomSelectActivity.this, WaitingActivity.class);
        intent.putExtra("top.criwits.sawa.NEW_ROOM_ID", roomID);
        startActivity(intent);
    }
}