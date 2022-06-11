package top.criwits.sawa.network;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

public class MessageQueue {
    private static final Queue<JSONObject> msgQueue = new LinkedList<>();

    public synchronized static void offer(JSONObject msg) {
        msgQueue.offer(msg);
    }

    public synchronized static JSONObject poll() {
        return msgQueue.poll();
    }

    public synchronized static boolean isEmpty() {
        return msgQueue.isEmpty();
    }
}
