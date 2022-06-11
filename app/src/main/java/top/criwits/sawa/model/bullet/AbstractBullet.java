package top.criwits.sawa.model.bullet;

import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.model.basic.AbstractFlyingObject;

/**
 * An abstract class for all kinds of bullets
 * @author hitsz
 */
public abstract class AbstractBullet extends AbstractFlyingObject {

    private int power = 10;

    public AbstractBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();

        // Outside the edge horizontally
        if (locationX <= 0 || locationX >= Graphics.screenWidth) {
            vanish();
        }

        // Outside the edge vertically
        if (speedY > 0 && locationY >= Graphics.screenHeight) {
            // Downside
            vanish();
        } else if (locationY <= 0) {
            // Upside
            vanish();
        }
    }

    public int getPower() {
        return power;
    }
}
