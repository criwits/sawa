package top.criwits.sawa.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;

import java.util.HashMap;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Media;

public class SoundHelper {
    private static HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();
    private static SoundPool soundPool;
    private static MediaPlayer player;
    private static Context context;

    public static void init(Context cnxt) {
        context = cnxt;
        // SoundPool 用来播放短的音频
        SoundPool.Builder builder = new SoundPool.Builder();
        // 传入音频数量
        builder.setMaxStreams(5);
        // AudioAttributes是一个封装音频各种属性的方法
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        // 设置音频流的合适的属性
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        // 加载一个AudioAttributes
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();
        soundID.put(2, soundPool.load(context, R.raw.bomb_explosion, 1));
        soundID.put(3, soundPool.load(context, R.raw.bullet_hit, 1));
        soundID.put(4, soundPool.load(context, R.raw.game_over, 1));
        soundID.put(5, soundPool.load(context, R.raw.get_supply, 1));
    }

    public static void startPlayingBOSSBGM() {
        if (Media.music) {
            player = MediaPlayer.create(context, R.raw.bgm_boss);
            player.setLooping(true);
            player.start();
        }
    }

    public static void stopPlayingBOSSBGM() {
        if (Media.music) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public static void playBombExplosion() {
        play(soundID.get(2), 1, 1, 0, 0,1);
    }

    public static void playBulletHit() {
        play(soundID.get(3), 1, 1, 0, 0,1);
    }

    public static void playGameOver() {
        play(soundID.get(4), 1, 1, 0, 0,1);
    }

    public static void playGetSupply() {
        play(soundID.get(5), 1, 1, 0, 0,1);
    }

    private static void play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        if (Media.music) {
            soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
    }

    private static void stop(int soundID) {
        if (Media.music) {
            soundPool.stop(soundID);
        }
    }

    public static void release() {
        if (Media.music && player != null) {
            player.release();
            player = null;
        }
    }
}