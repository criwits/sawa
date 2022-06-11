package top.criwits.sawa.model.bullet;

import java.util.List;

public interface BulletStrategy {
    public List<AbstractBullet> generateBullets(int type, int count, int direction, int locationX, int locationY, int speedX, int speedY, int power);
}
