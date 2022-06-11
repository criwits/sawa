package top.criwits.sawa.model.aircraft;

import top.criwits.sawa.model.bullet.AbstractBullet;
import top.criwits.sawa.model.bullet.BulletStrategy;

import java.util.List;

/**
 * "A cannon is a large-caliber gun classified as a type of artillery,
 * and usually launches a projectile using explosive chemical propellant."
 * @author hans
 */
public class Cannon {
    private int bulletType;
    private int bulletDirection;
    private int bulletPower;
    private int bulletCount;
    private BulletStrategy strategy;

    public Cannon(int bulletType, int bulletDirection, int bulletPower, int bulletCount, BulletStrategy strategy) {
        this.bulletDirection = bulletDirection;
        this.bulletType = bulletType;
        this.bulletPower = bulletPower;
        this.bulletCount = bulletCount;
        this.strategy = strategy;
    }

    public void setPower(int power) {
        this.bulletPower = power;
    }

    public void setCount(int count) {
        this.bulletCount = count;
    }
    public int getCount() {
        return bulletCount;
    }

    public void setStrategy(BulletStrategy strategy) {
        this.strategy = strategy;
    }

    public List<AbstractBullet> shoot(AbstractAircraft aircraft) {
        return strategy.generateBullets(bulletType, bulletCount, bulletDirection,
                aircraft.getLocationX(),
                aircraft.getLocationY(),
                aircraft.getSpeedX(),
                aircraft.getSpeedY(),
                bulletPower);

    }
}
