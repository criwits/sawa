package top.criwits.sawa.multi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

import top.criwits.sawa.aircraft.AbstractAircraft;
import top.criwits.sawa.aircraft.AircraftFactory;
import top.criwits.sawa.aircraft.BossEnemy;
import top.criwits.sawa.aircraft.EliteEnemy;
import top.criwits.sawa.aircraft.EliteEnemyFactory;
import top.criwits.sawa.aircraft.FriendAircraft;
import top.criwits.sawa.aircraft.HeroAircraft;
import top.criwits.sawa.aircraft.MobEnemy;
import top.criwits.sawa.aircraft.MobEnemyFactory;
import top.criwits.sawa.basic.AbstractFactory;
import top.criwits.sawa.basic.AbstractFlyingObject;
import top.criwits.sawa.bullet.AbstractBullet;
import top.criwits.sawa.config.AircraftHP;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.Kinematics;
import top.criwits.sawa.config.Probability;
import top.criwits.sawa.media.ImageManager;
import top.criwits.sawa.media.SoundHelper;
import top.criwits.sawa.network.MessageQueue;
import top.criwits.sawa.network.WSService;
import top.criwits.sawa.prop.AbstractProp;
import top.criwits.sawa.utils.RandomGenerator;

public class GameLogic {
    private int score = 0;
    private int lastTimeBossSpawned = 0;
    private int difficultyIncreaseCount = 0;

    private final List<AbstractAircraft> enemyAircraft;
    private final List<AbstractBullet> heroBullets;
    private final List<AbstractBullet> friendBullets;
    private final List<AbstractBullet> enemyBullets;
    private final List<AbstractProp> props;

    private boolean gameOver = false;

    public int getScore() {
        return score;
    }

    public List<AbstractAircraft> getEnemyAircraftList() {
        return enemyAircraft;
    }

    public List<AbstractBullet> getHeroBulletsList() {
        return heroBullets;
    }

    public List<AbstractBullet> getFriendBullets() {
        return friendBullets;
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
        friendBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        // 精英机加载
        HeroAircraft.loadInstance(
                Graphics.screenWidth / 2,
                Graphics.screenHeight - ImageManager.HERO_IMG.getHeight() ,
                0, 0, AircraftHP.heroAircraftHP);

        FriendAircraft.loadInstance(
                Graphics.screenWidth / 2,
                Graphics.screenHeight - ImageManager.HERO_IMG.getHeight() ,
                0, 0, AircraftHP.heroAircraftHP);

        BossEnemy.resetBoss();
    }

    public void doAtEveryCycle() {
        shootAction();
    }

    // boolean msgFlag = false;

    public void doAtEveryTick() {
        // Send movement
        // 下面这行代码是在每一帧的开始时向服务器发送位置信息的
       //  msgFlag = !msgFlag;
       //  if (msgFlag) {
            WSService.getClient().send("{\"type\": \"movement\", \"new_x\": " + (int) (HeroAircraft.getInstance().getLocationX() / Graphics.pixelScalingFactor) +
                    ", \"new_y\": " + (int) (HeroAircraft.getInstance().getLocationY() / Graphics.pixelScalingFactor) + "}");
        // }
        // Fetch Messages
        while (!MessageQueue.isEmpty()) {
            JSONObject msg = MessageQueue.poll();
            switch (msg.getString("type")) {
                case "teammate_movement":
                    FriendAircraft.setLocation((int) (msg.getInteger("new_x") * Graphics.pixelScalingFactor),
                            (int) (msg.getInteger("new_y") * Graphics.pixelScalingFactor));
                    break;
                case "npc_spawn":
                    AircraftFactory factory = null;
                    switch (msg.getInteger("mob")) {
                        case 0:
                            factory = new MobEnemyFactory();
                            break;
                        case 1:
                            factory = new EliteEnemyFactory();
                            break;
                        default:
                            break;
                    }
                    assert factory != null;
                    AbstractAircraft newAircraft = factory.createAircraft(
                            (int)(msg.getInteger("location_x") * Graphics.pixelScalingFactor),
                            (int)(msg.getInteger("location_y") * Graphics.pixelScalingFactor),
                            (int)(msg.getInteger("speed_x") * Graphics.pixelScalingFactor),
                            (int)(msg.getInteger("speed_y") * Graphics.pixelScalingFactor),
                            msg.getInteger("hp")
                    );
                    newAircraft.setId(msg.getInteger("id"));
                    enemyAircraft.add(newAircraft);
                    break;
                default:
                    break;
            }
        }

        // Increase difficulty
        difficultyIncrease();
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

        if (HeroAircraft.getInstance().getHp() <= 0) {
            gameOver = true;
        }
    }


    private void shootAction() {
        // Enemies
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        // Hero
        heroBullets.addAll(HeroAircraft.getInstance().shoot());
        // Friend
        friendBullets.addAll(FriendAircraft.getInstance().shoot());
    }

    private void bulletsMoveAction() {
        for (AbstractBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (AbstractBullet bullet: friendBullets) {
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

        // Friend bullets
        for (AbstractBullet bullet: friendBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft: enemyAircraft) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    SoundHelper.playBulletHit();
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
        friendBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircraft.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    private void difficultyIncrease() {
        if (Difficulty.difficulty != 0) {
            difficultyIncreaseCount++;
            if (difficultyIncreaseCount == Difficulty.difficultyIncreaseCycleCount) {
                difficultyIncreaseCount = 0;
                // Decrease boss threshold
                if (Difficulty.bossScoreThreshold > Difficulty.bossScoreThresholdMinimum) {
                    Difficulty.bossScoreThreshold -= Difficulty.bossScoreThresholdDecrease;
                }
                // Elite probability
                if (Probability.eliteProbability < Difficulty.eliteEnemyProbabilityMaximum) {
                    Probability.eliteProbability += Difficulty.eliteEnemyProbabilityIncrease;
                }
                Kinematics.enemySpeedY += Difficulty.enemySpeedYIncrease;
                System.out.printf("Boss HP: %d, Boss score threshold: %d, Elite probability: %f, Enemy Speed Y: %d\n",
                        AircraftHP.bossEnemyHP, Difficulty.bossScoreThreshold, Probability.eliteProbability, Kinematics.enemySpeedY);
            }
        }
    }

    public void moveHeroAircraft(int deltaX, int deltaY) {
        HeroAircraft.getInstance().move(deltaX, deltaY);
    }

    public boolean getGameOver() {
        return gameOver;
    }


}
