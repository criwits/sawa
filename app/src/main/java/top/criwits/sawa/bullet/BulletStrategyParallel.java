package top.criwits.sawa.bullet;

import top.criwits.sawa.aircraft.AbstractAircraft;
import top.criwits.sawa.config.Kinematics;

import java.util.LinkedList;
import java.util.List;

public class BulletStrategyParallel implements BulletStrategy {
    @Override
    public List<AbstractBullet> generateBullets(int type, int count, int direction, int locationX, int locationY, int speedX, int speedY, int power) {
        List<AbstractBullet> res = new LinkedList<>();
        int x = locationX;
        int y = locationY + direction * Kinematics.getRealPixel(1);
        int sX = speedX;
        int sY = speedY + direction * Kinematics.getRealPixel(3);    // Same as above.
        AbstractBullet abstractBullet;
        for(int i = 0; i < count; i++) {
            // To make bullets separate horizontally.
            if (type == 0) {
                abstractBullet = new HeroBullet(x + Kinematics.getRealPixel((i * 2 - count + 1) * 10), y, sX, sY, power);
            } else {
                abstractBullet = new EnemyBullet(x + Kinematics.getRealPixel((i * 2 - count + 1) * 10), y, sX, sY, power);
            }

            res.add(abstractBullet);
        }
        return res;
    }
}
