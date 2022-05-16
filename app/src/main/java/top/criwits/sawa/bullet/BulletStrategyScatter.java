package top.criwits.sawa.bullet;

import java.util.LinkedList;
import java.util.List;

public class BulletStrategyScatter implements BulletStrategy{
    @Override
    public List<AbstractBullet> generateBullets(int type, int count, int direction, int locationX, int locationY, int speedX, int speedY, int power) {
        List<AbstractBullet> res = new LinkedList<>();
        int x = locationX;
        int y = locationY + direction * 2;
        int sX = speedX;
        int sY = speedY + direction * 5;    // Same as above.
        AbstractBullet abstractBullet;
        for(int i = 0; i < count; i++) {
            // To make bullets separate horizontally.
            if (type == 0) {
                abstractBullet = new HeroBullet(x, y, sX + i * 2 - count + 1, sY, power);
            } else {
                abstractBullet = new EnemyBullet(x, y, sX + i * 2 - count + 1, sY, power);
            }
            res.add(abstractBullet);
        }
        return res;
    }
}
