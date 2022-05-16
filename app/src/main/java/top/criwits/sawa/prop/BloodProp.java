package top.criwits.sawa.prop;

import top.criwits.sawa.aircraft.AbstractAircraft;
import top.criwits.sawa.aircraft.HeroAircraft;
import top.criwits.sawa.bullet.AbstractBullet;

import java.util.List;

public class BloodProp extends AbstractProp {
    public BloodProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }
    
    @Override
    public int action(HeroAircraft heroAircraft, List<AbstractAircraft> enemyAircraft, List<AbstractBullet> abstractBullets) {
        // PlaySound.playGetSupplySound();
        heroAircraft.increaseHp(20);
        return 0;
    }
}