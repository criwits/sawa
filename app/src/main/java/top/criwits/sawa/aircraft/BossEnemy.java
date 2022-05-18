package top.criwits.sawa.aircraft;

import top.criwits.sawa.bullet.AbstractBullet;
import top.criwits.sawa.bullet.BulletStrategyScatter;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.Kinematics;
import top.criwits.sawa.config.Probability;
import top.criwits.sawa.media.SoundHelper;
import top.criwits.sawa.prop.*;


import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends AbstractAircraft{

    private static BossEnemy instance = null;

    private BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    public static void resetBoss() {
        instance = null;
    }

    public static boolean isBossActive () {
        if (instance == null) {
            return false;
        } else {
            return !instance.notValid();
        }
    }

    @Override
    public void forward() {
        super.forward();
        // Check if this aircraft is out of edge
        if (locationY >= Graphics.screenHeight ) {
            vanish();
        }
    }

    public static BossEnemy getInstance() {
        return instance;
    }

    public static synchronized BossEnemy summonBoss(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (isBossActive()) {
            return null;
        } else {
            // when boss spawns the BGM should play
            SoundHelper.startPlayingBOSSBGM();
            instance = new BossEnemy(locationX, locationY, speedX, speedY, hp);
            return getInstance();
        }
    }

    /** Attack constants */
    public Cannon cannon = new Cannon(1, 1, Difficulty.bossBulletPower, Difficulty.bossBulletCount, new BulletStrategyScatter());
    @Override
    public List<AbstractBullet> shoot() {
        return cannon.shoot(this);
    }

    @Override
    public List<AbstractProp> generateProp() {
        List<AbstractProp> propList = new LinkedList<>();
        for (int i = 0; i < Difficulty.bossPropCount; i++) {
            double decision = Math.random();
            PropFactory newPropFactory;

            if (decision < Probability.bloodPropProbability) {
                newPropFactory = new BloodPropFactory();
            } else if (decision - Probability.bloodPropProbability < Probability.bombPropProbability) {
                newPropFactory = new BombPropFactory();
            } else if (decision - Probability.bloodPropProbability - Probability.bombPropProbability < Probability.bulletPropProbability) {
                newPropFactory = new BulletPropFactory();
            } else {
                return propList;
            }
            propList.add(newPropFactory.createProp(
                    this.getLocationX() + Kinematics.getRealPixel((int)(Math.random() * 50 - 25)),
                    this.getLocationY() + Kinematics.getRealPixel((int)(Math.random() * 50 - 25)),
                    0,
                    Kinematics.getRealPixel(Kinematics.enemySpeedY)));
        }
        return propList;
    }

    @Override
    public int addScore() {
        return Difficulty.bossEnemyScore;
    }

    @Override
    public void vanish() {
        isValid = false;
        // when boss dies the BGM of boss should stop
        SoundHelper.stopPlayingBOSSBGM();
    }

}
