package top.criwits.sawa.network;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class WSConn implements ServiceConnection {
    private WSService.WSBinder binder;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        binder = (WSService.WSBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public WSService.WSBinder getBinder() {
        return binder;
    }
}
