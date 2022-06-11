package top.criwits.sawa.model.prop;

import top.criwits.sawa.model.aircraft.AbstractAircraft;
import top.criwits.sawa.model.aircraft.HeroAircraft;
import top.criwits.sawa.model.basic.AbstractFlyingObject;
import top.criwits.sawa.model.bullet.AbstractBullet;
import top.criwits.sawa.config.Graphics;

import java.util.List;

/**
 * An abstract class for all kinds of props
 */
public abstract class AbstractProp extends AbstractFlyingObject {
    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    // Copied from AbstractBullet
    @Override
    public void forward() {
        super.forward();
        // Outside the edge horizontally
        if (locationX <= 0 || locationX >= Graphics.screenWidth) {
            vanish();
        }
        // Outside the edge vertically
        if (speedY > 0 && locationY >= Graphics.screenHeight ) {
            // Downside
            vanish();
        }else if (locationY <= 0){
            // Upside
            vanish();
        }
    }

    public abstract int action(HeroAircraft heroAircraft,
                               List<AbstractAircraft> enemyAircraft,
                               List<AbstractBullet> abstractBullets);
}