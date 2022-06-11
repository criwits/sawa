package top.criwits.sawa.model.prop;

import top.criwits.sawa.model.aircraft.AbstractAircraft;
import top.criwits.sawa.model.aircraft.HeroAircraft;
import top.criwits.sawa.model.bullet.AbstractBullet;
import top.criwits.sawa.media.SoundHelper;

import java.util.List;

public class BloodProp extends AbstractProp {
    public BloodProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }
    
    @Override
    public int action(HeroAircraft heroAircraft, List<AbstractAircraft> enemyAircraft, List<AbstractBullet> abstractBullets) {
        SoundHelper.playGetSupply();
        heroAircraft.increaseHp(50);
        return 0;
    }
}