package top.criwits.sawa.prop;

import top.criwits.sawa.aircraft.AbstractAircraft;
import top.criwits.sawa.aircraft.BossEnemy;
import top.criwits.sawa.aircraft.HeroAircraft;
import top.criwits.sawa.basic.AbstractFlyingObject;
import top.criwits.sawa.bullet.AbstractBullet;

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
        // PlaySound.playBombExplosionSound();
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