package top.criwits.sawa.aircraft;

import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.Kinematics;
import top.criwits.sawa.bullet.AbstractBullet;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.prop.AbstractProp;

import java.util.LinkedList;
import java.util.List;

/**
 * Normal Enemy (MobEnemy)
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // Check if this aircraft is out of edge
        if (locationY >= Graphics.screenHeight) {
            vanish();
        }
    }

    @Override
    public List<AbstractBullet> shoot() {
        return new LinkedList<>();
    }

    @Override
    public List<AbstractProp> generateProp() {
        return new LinkedList<>();
    }

    @Override
    public int addScore() {
        return Difficulty.mobEnemyScore;
    }

}
