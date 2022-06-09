package top.criwits.sawa.aircraft;

import top.criwits.sawa.bullet.AbstractBullet;
import top.criwits.sawa.bullet.BulletStrategyParallel;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.Probability;
import top.criwits.sawa.network.WSService;
import top.criwits.sawa.prop.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Elite Enemy (EliteEnemy)
 * Will shoot bullets at a specific frequency.
 * @author hitsz
 */
public class EliteEnemy extends AbstractAircraft {

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // Check if this aircraft is out of edge
        if (locationY >= Graphics.screenHeight ) {
            vanish();
        }
    }

    /** Attack constants */
    public Cannon cannon = new Cannon(1, 1, Difficulty.enemyBulletPower, 1, new BulletStrategyParallel());
    @Override
    public List<AbstractBullet> shoot() {
        return cannon.shoot(this);
    }
    @Override
    public List<AbstractProp> generateProp() {
        List<AbstractProp> propList = new LinkedList<>();
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
                this.getLocationX(),
                this.getLocationY(),
                0,
                this.getSpeedY()));
        return propList;
    }

    @Override
    public int addScore() {
        return Difficulty.eliteEnemyScore;
    }

}
