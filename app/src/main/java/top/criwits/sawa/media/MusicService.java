package top.criwits.sawa.media;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;

import java.util.HashMap;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Media;

public class MusicService extends Service {
    private static final String TAG = "SAWAMusicService";
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();

    private MediaPlayer player;

    public MusicService() {

    }


    @Override
    public IBinder onBind(Intent intent){
        startPlayingBGM();
        SoundHelper.init(getApplicationContext());
        return null;
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 播放 BGM
     */
    public void startPlayingBGM(){
        if (Media.music) {
            if(player == null){
                player = MediaPlayer.create(this, R.raw.bgm);
                player.setLooping(true);
            }
            player.start();
        }
    }

    /**
     * 停止播放 BGM
     */
    public void stopPlayingBGM() {
        if (Media.music) {
            if (player != null) {
                player.stop();
                player.reset();
                player.release();
                player = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayingBGM();
        SoundHelper.release();
    }

}