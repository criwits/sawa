package top.criwits.sawa.model.aircraft;

import top.criwits.sawa.model.bullet.*;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.model.prop.AbstractProp;

import java.util.LinkedList;
import java.util.List;

/**
 * HeroAircraft controlled by the player
 * Singleton Pattern
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    /** Singleton pattern obj. **/
    private static HeroAircraft instance;
    /** Initialisation function **/
    public static synchronized void loadInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
    }
    /** Get instance **/
    public static HeroAircraft getInstance() {
        return instance;
    }

    /** Attack constants */
    public Cannon cannon = new Cannon(0, -1, Difficulty.heroBulletPower, 1, new BulletStrategyParallel());

    public void move(int deltaX, int deltaY) {
        if (0 <= deltaX + locationX && deltaX + locationX <= Graphics.screenWidth) {
            locationX = deltaX + locationX;
        }
        if (0 <= deltaY + locationY && deltaY + locationY <= Graphics.screenHeight) {
            locationY = deltaY + locationY;
        }
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
