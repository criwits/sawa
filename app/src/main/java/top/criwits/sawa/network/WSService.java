package top.criwits.sawa.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WSService extends Service {

    private static WebSocketClient client = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String address = intent.getStringExtra("top.criwits.sawa.MULTIADDR");
        System.out.println("Server address " + address);
        client = new WebSocketClient(URI.create(address)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("WebSocket connection established!");
                Intent intent = new Intent("top.criwits.sawa.CONNECTION");
                intent.putExtra("top.criwits.sawa.CONNSTATUS", true);
                sendBroadcast(intent);
            }

            @Override
            public void onMessage(String message) {
                JSONObject msg = JSON.parseObject(message);
                switch (msg.getString("type")) {
                    case "user_query_response":
                    case "room_info_response":
                    case "create_room_response":
                    case "join_room_response":
                    case "room_ready":
                    case "game_start":
                        System.out.println("Received message: " + message);
                        Intent intent = new Intent("top.criwits.sawa.MESSAGE");
                        intent.putExtra("top.criwits.sawa.MESSAGE_RAW", message);
                        sendBroadcast(intent);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed for reason: " + reason);
                Intent intent = new Intent("top.criwits.sawa.CONNECTION");
                intent.putExtra("top.criwits.sawa.CONNSTATUS", false);
                sendBroadcast(intent);
                WSService.super.onDestroy();
            }

            @Override
            public void onError(Exception ex) {
                WSService.super.onDestroy();
                ex.printStackTrace();
            }
        };

        client.connect();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        client.close();
        client = null;
        super.onDestroy();
    }

    public static WebSocketClient getClient() {
        return client;
    }


}
