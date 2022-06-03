package top.criwits.sawa.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WSService extends Service {
    public WebSocketClient client;

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
            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed for reason: " + reason);
                Intent intent = new Intent("top.criwits.sawa.CONNECTION");
                intent.putExtra("top.criwits.sawa.CONNSTATUS", false);
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
        super.onDestroy();
    }
}
