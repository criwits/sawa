package top.criwits.sawa.solo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.LinkedList;
import java.util.List;

import top.criwits.sawa.aircraft.AbstractAircraft;
import top.criwits.sawa.aircraft.AircraftFactory;
import top.criwits.sawa.aircraft.EliteEnemyFactory;
import top.criwits.sawa.aircraft.HeroAircraft;
import top.criwits.sawa.aircraft.MobEnemyFactory;
import top.criwits.sawa.basic.AbstractFlyingObject;
import top.criwits.sawa.bullet.AbstractBullet;
import top.criwits.sawa.config.AircraftHP;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.Kinematics;
import top.criwits.sawa.config.Probability;
import top.criwits.sawa.media.ImageManager;
import top.criwits.sawa.media.SoundHelper;
import top.criwits.sawa.prop.AbstractProp;
import top.criwits.sawa.utils.RandomGenerator;

public class GameLogic {
    private int score = 0;
    private final List<AbstractAircraft> enemyAircraft;
    private final List<AbstractBullet> heroBullets;
    private final List<AbstractBullet> enemyBullets;
    private final List<AbstractProp> props;

    public int getScore() {
        return score;
    }

    public List<AbstractAircraft> getEnemyAircraftList() {
        return enemyAircraft;
    }

    public List<AbstractBullet> getHeroBulletsList() {
        return heroBullets;
    }

    public List<AbstractBullet> getEnemyBulletsList() {
        return enemyBullets;
    }

    public List<AbstractProp> getPropsList() {
        return props;
    }

    public GameLogic() {
        // 初始化各个 List
        enemyAircraft = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        // 精英机加载
        HeroAircraft.loadInstance(
                Graphics.screenWidth / 2,
                Graphics.screenHeight - ImageManager.HERO_IMG.getHeight() ,
                0, 0, AircraftHP.heroAircraftHP);
    }

    public void doAtEveryCycle() {
        // Spawn of enemies if can
        if (enemyAircraft.size() < Difficulty.enemyMaxNumber) {
            AircraftFactory newAircraftFactory;
            int speedX;
            int hp;
            // Decide which type of enemy should be spawned
            if (Math.random() < Probability.eliteProbability) {
                newAircraftFactory = new EliteEnemyFactory();
                speedX = RandomGenerator.nonZeroGenerator(Kinematics.enemySpeedX);
                hp = AircraftHP.eliteEnemyHP;
            } else {
                newAircraftFactory = new MobEnemyFactory();
                speedX = 0;
                hp = AircraftHP.mobEnemyHP;
            }
            enemyAircraft.add(newAircraftFactory.createAircraft(
                    (int) (Math.random() * (Graphics.screenWidth - ImageManager.MOB_IMG.getWidth())),
                    (int) (Math.random() * Graphics.screenHeight * 0.2),
                    speedX,
                    Kinematics.enemySpeedY,
                    hp
            ));
        }
        // Shoot
        shootAction();
    }

    public void doAtEveryTick() {
        // Increase difficulty
        difficultyIncrease();
        // Boss Generation
        bossGenerateAction(score, enemyAircraft);
        // Bullet move
        bulletsMoveAction();
        // Aircraft move
        aircraftsMoveAction();
        // props move
        propsMoveAction();
        // Crash check
        crashCheckAction();
        // Post process
        postProcessAction();
    }

    protected void bossGenerateAction(int score, List<AbstractAircraft> enemyAircrafts) {};

    private void shootAction() {
        // Enemies
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        // Hero
        heroBullets.addAll(HeroAircraft.getInstance().shoot());
    }

    private void bulletsMoveAction() {
        for (AbstractBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (AbstractBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }


    /** Hit box detection */
    private void crashCheckAction() {
        // Enemy bullets
        for (AbstractBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if(HeroAircraft.getInstance().crash(bullet)) {
                HeroAircraft.getInstance().decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // Player bullets
        for (AbstractBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircraft) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    SoundHelper.playBulletHit();
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                }

            }
        }

        // Props spawn
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            if (enemyAircraft.notValid()) {
                // Add score!
                score += enemyAircraft.addScore();
                props.addAll(enemyAircraft.generateProp());
            }
        }

        // Enemy and Hero crash
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            if (enemyAircraft.crash(HeroAircraft.getInstance()) || HeroAircraft.getInstance().crash(enemyAircraft)) {
                enemyAircraft.vanish();
                HeroAircraft.getInstance().decreaseHp(Integer.MAX_VALUE);
            }
        }

        for (AbstractProp prop: props) {
            if (HeroAircraft.getInstance().crash(prop)) {
                score += prop.action(HeroAircraft.getInstance(), enemyAircraft, enemyBullets);
                prop.vanish();
            }
        }

    }

    /**
     * Post process
     *   - Remove invalid enemies
     *   - Remove invalid bullets
     *   - Remove invalid & used props
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircraft.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    protected void difficultyIncrease() {}

    public void moveHeroAircraft(int deltaX, int deltaY) {
        HeroAircraft.getInstance().move(deltaX, deltaY);
    }



}
