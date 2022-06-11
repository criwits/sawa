package top.criwits.sawa.model.prop;

import top.criwits.sawa.model.aircraft.AbstractAircraft;
import top.criwits.sawa.model.aircraft.BossEnemy;
import top.criwits.sawa.model.aircraft.HeroAircraft;
import top.criwits.sawa.model.basic.AbstractFlyingObject;
import top.criwits.sawa.model.bullet.AbstractBullet;
import top.criwits.sawa.media.SoundHelper;

import java.util.LinkedList;
import java.util.List;

public class BombProp extends AbstractProp {
    private List<AbstractFlyingObject> flyingObjects = new LinkedList<>();

    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public int action(HeroAircraft heroAircraft, List<AbstractAircraft> enemyAircraft, List<AbstractBullet> abstractBullets) {
        // subscribe
        addAircraft(enemyAircraft, abstractBullets);
        // work
        SoundHelper.playBombExplosion();
        return notifyAllFlyingObjects();
    }

    private void addAircraft(List<AbstractAircraft> enemyAircraft, List<AbstractBullet> abstractBullets) {
        this.flyingObjects.addAll(enemyAircraft);
        this.flyingObjects.addAll(abstractBullets);
    }

    private int notifyAllFlyingObjects() {
        int totalScore = 0;
        for (AbstractFlyingObject abstractFlyingObject : this.flyingObjects) {
            if (abstractFlyingObject instanceof BossEnemy) {
                continue;
            }
            abstractFlyingObject.vanish();
            if (abstractFlyingObject instanceof AbstractAircraft) {
                totalScore += ((AbstractAircraft) abstractFlyingObject).addScore();
            }
        }
        return totalScore;
    }
}