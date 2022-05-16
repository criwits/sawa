package top.criwits.sawa.utils;

import android.content.res.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

import top.criwits.sawa.R;
import top.criwits.sawa.aircraft.BossEnemy;
import top.criwits.sawa.aircraft.EliteEnemy;
import top.criwits.sawa.aircraft.HeroAircraft;
import top.criwits.sawa.aircraft.MobEnemy;
import top.criwits.sawa.bullet.EnemyBullet;
import top.criwits.sawa.bullet.HeroBullet;
import top.criwits.sawa.prop.BloodProp;
import top.criwits.sawa.prop.BombProp;
import top.criwits.sawa.prop.BulletProp;

public class ImageManager {

    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static Bitmap BG_IMG;
    public static Bitmap BG2_IMG;
    public static Bitmap BG3_IMG;
    public static Bitmap BG4_IMG;
    public static Bitmap BG5_IMG;
    public static Bitmap BOSS_IMG;
    public static Bitmap BULLET_ENEMY_IMG;
    public static Bitmap BULLET_HERO_IMG;
    public static Bitmap ELITE_IMG;
    public static Bitmap HERO_IMG;
    public static Bitmap MOB_IMG;
    public static Bitmap PROP_BLOOD_IMG;
    public static Bitmap PROP_BOMB_IMG;
    public static Bitmap PROP_BULLET_IMG;

    /**
     * 将贴图和对象绑定
     */
    static {
        CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMG);
        CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_IMG);
        CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), BULLET_HERO_IMG);
        CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), BULLET_ENEMY_IMG);
        CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_IMG);
        CLASSNAME_IMAGE_MAP.put(BloodProp.class.getName(), PROP_BLOOD_IMG);
        CLASSNAME_IMAGE_MAP.put(BombProp.class.getName(), PROP_BOMB_IMG);
        CLASSNAME_IMAGE_MAP.put(BulletProp.class.getName(), PROP_BULLET_IMG);
        CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_IMG);
    }

    /**
     * 加载图像文件
     * @param r getResource() 的返回值
     */
    public static void loadImages(Resources r) {
        BG_IMG = BitmapFactory.decodeResource(r, R.drawable.bg);
        BG2_IMG = BitmapFactory.decodeResource(r, R.drawable.bg2);
        BG3_IMG = BitmapFactory.decodeResource(r, R.drawable.bg3);
        BG4_IMG = BitmapFactory.decodeResource(r, R.drawable.bg4);
        BG5_IMG = BitmapFactory.decodeResource(r, R.drawable.bg5);
        BOSS_IMG = BitmapFactory.decodeResource(r, R.drawable.boss);
        BULLET_ENEMY_IMG = BitmapFactory.decodeResource(r, R.drawable.bullet_enemy);
        BULLET_HERO_IMG = BitmapFactory.decodeResource(r, R.drawable.bullet_hero);
        ELITE_IMG = BitmapFactory.decodeResource(r, R.drawable.elite);
        HERO_IMG = BitmapFactory.decodeResource(r, R.drawable.hero);
        MOB_IMG = BitmapFactory.decodeResource(r, R.drawable.mob);
        PROP_BLOOD_IMG = BitmapFactory.decodeResource(r, R.drawable.prop_blood);
        PROP_BOMB_IMG = BitmapFactory.decodeResource(r, R.drawable.prop_bomb);
        PROP_BULLET_IMG = BitmapFactory.decodeResource(r, R.drawable.prop_bullet);
    }


    public static Bitmap get(String className){
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object obj){
        if (obj == null){
            return null;
        }
        return get(obj.getClass().getName());
    }


}
