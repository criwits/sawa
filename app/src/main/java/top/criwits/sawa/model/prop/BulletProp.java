package top.criwits.sawa.model.prop;

import top.criwits.sawa.model.aircraft.AbstractAircraft;
import top.criwits.sawa.model.aircraft.HeroAircraft;
import top.criwits.sawa.model.bullet.AbstractBullet;
import top.criwits.sawa.model.bullet.BulletStrategyParallel;
import top.criwits.sawa.model.bullet.BulletStrategyScatter;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.media.SoundHelper;
// import top.criwits.sawa.sound.PlaySound;

import java.util.List;

public class BulletProp extends AbstractProp {
    private static int strategyLevel = 0;

    public BulletProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public int action(HeroAircraft heroAircraft, List<AbstractAircraft> enemyAircraft, List<AbstractBullet> abstractBullets) {
        // play sound
        SoundHelper.playGetSupply();
        // add more bullets
        heroAircraft.cannon.setCount(heroAircraft.cannon.getCount() + Difficulty.bulletPropEffectLevel);
        heroAircraft.cannon.setStrategy(new BulletStrategyScatter());
        // increase strategy level
        strategyLevel++;
        Runnable r = () -> {
            try {
                // sleep for a period of time
                Thread.sleep(Difficulty.bulletPropEffectTime);
                // recover
                heroAircraft.cannon.setCount(heroAircraft.cannon.getCount() - Difficulty.bulletPropEffectLevel);
                strategyLevel--;
                if (strategyLevel == 0) {
                    // prevent when multiple bullet props are active,
                    // the first one may erase others' effect.
                    heroAircraft.cannon.setStrategy(new BulletStrategyParallel());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(r, "BulletPropTimer").start();
        return 0;
    }

}