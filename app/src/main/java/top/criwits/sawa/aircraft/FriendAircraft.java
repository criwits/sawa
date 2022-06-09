package top.criwits.sawa.aircraft;

import top.criwits.sawa.bullet.*;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.prop.AbstractProp;

import java.util.LinkedList;
import java.util.List;

/**
 * HeroAircraft controlled by another player
 * Singleton Pattern
 * @author hitsz
 */
public class FriendAircraft extends AbstractAircraft {

    private FriendAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    /** Singleton pattern obj. **/
    private static FriendAircraft instance;
    /** Initialisation function **/
    public static synchronized void loadInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        instance = new FriendAircraft(locationX, locationY, speedX, speedY, hp);
    }
    /** Get instance **/
    public static FriendAircraft getInstance() {
        return instance;
    }

    /** Attack constants */
    public Cannon cannon = new Cannon(0, -1, Difficulty.heroBulletPower, 1, new BulletStrategyParallel());

    public static void setLocation(int locationX, int locationY) {
        instance.locationX = locationX;
        instance.locationY = locationY;
    }

    @Override
    public List<AbstractBullet> shoot() {
        return cannon.shoot(this);
    }


    @Override
    public void forward() {
        // The HeroAircraft is controlled by player using mouse pointer,
        // so it's nothing here!
    }

    @Override
    public List<AbstractProp> generateProp() {
        return new LinkedList<>();
    }

    @Override
    public int addScore() {
        return 0;
    }

}
