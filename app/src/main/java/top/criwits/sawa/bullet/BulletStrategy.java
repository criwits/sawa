package top.criwits.sawa.bullet;

import top.criwits.sawa.aircraft.AbstractAircraft;

import java.util.List;

public interface BulletStrategy {
    public List<AbstractBullet> generateBullets(int type, int count, int direction, int locationX, int locationY, int speedX, int speedY, int power);
}
