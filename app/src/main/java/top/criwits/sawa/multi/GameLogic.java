package top.criwits.sawa.multi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

import top.criwits.sawa.model.aircraft.AbstractAircraft;
import top.criwits.sawa.model.aircraft.AircraftFactory;
import top.criwits.sawa.model.aircraft.BossEnemy;
import top.criwits.sawa.model.aircraft.EliteEnemyFactory;
import top.criwits.sawa.model.aircraft.FriendAircraft;
import top.criwits.sawa.model.aircraft.HeroAircraft;
import top.criwits.sawa.model.aircraft.MobEnemyFactory;
import top.criwits.sawa.model.basic.AbstractFlyingObject;
import top.criwits.sawa.model.bullet.AbstractBullet;
import top.criwits.sawa.model.bullet.BulletStrategyParallel;
import top.criwits.sawa.model.bullet.BulletStrategyScatter;
import top.criwits.sawa.config.AircraftHP;
import top.criwits.sawa.config.Difficulty;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.config.Kinematics;
import top.criwits.sawa.config.Multiple;
import top.criwits.sawa.config.Probability;
import top.criwits.sawa.media.ImageManager;
import top.criwits.sawa.media.SoundHelper;
import top.criwits.sawa.network.MessageQueue;
import top.criwits.sawa.network.WSService;
import top.criwits.sawa.model.prop.AbstractProp;
import top.criwits.sawa.model.prop.BloodPropFactory;
import top.criwits.sawa.model.prop.BombPropFactory;
import top.criwits.sawa.model.prop.BulletPropFactory;
import top.criwits.sawa.model.prop.PropFactory;
import top.criwits.sawa.utils.RandomGenerator;

public class GameLogic {
    private int score = 0;
    private int lastTimeBossSpawned = 0;
    private int difficultyIncreaseCount = 0;
    private int id = 0;
    private static int playerStrategyLevel = 0;
    private static int friendStrategyLevel = 0;

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
        // ??????????????? List
        enemyAircraft = new LinkedList<>();
        heroBullets = new LinkedList<>();
        friendBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        // ???????????????
        HeroAircraft.loadInstance(
                Graphics.screenWidth / 2,
                Graphics.screenHeight - ImageManager.HERO_IMG.getHeight(),
                0, 0, AircraftHP.heroAircraftHP);

        FriendAircraft.loadInstance(
                Graphics.screenWidth / 2,
                Graphics.screenHeight - ImageManager.HERO_IMG.getHeight(),
                0, 0, AircraftHP.heroAircraftHP);

        BossEnemy.resetBoss();
    }

    public void doAtEveryCycle() {
        // Spawn (generate) NPCs, and upload their information
        spawnNPCandUpload();
        // Let all NPCs shoot
        shootAction();
    }

    public void doAtEveryTick() {

        // Upload hero movement
        uploadHeroMovement();
        // Fetch messages
        fetchMessages();
        // Boss Generation
        bossGenerateAction();
        // Increase difficulty
        difficultyIncrease();
        // Bullet move
        bulletsMoveAction();
        // Aircraft move
        aircraftsMoveAction();
        // Upload enemy out of screen
        uploadEnemyOutScreen();
        // Upload prop out of screen
        uploadPropOutScreen();
        // props move
        propsMoveAction();
        // Crash check
        crashCheckAction();
        // Post process
        postProcessAction();
        // Game over check
        gameOverCheck();
    }

    private void uploadPropOutScreen() {
        if (Multiple.isHost) {
            for (AbstractProp prop : props) {
                if (prop.getLocationY() >= Graphics.screenHeight) {
                    WSService.getClient().send("{\"type\": \"remove_prop\",\"remove\":" + prop.getId() + "}");
                }
            }
        }
    }

    private void bossGenerateAction() {
        if (Multiple.isHost) {
            if (Difficulty.difficulty != 0) {

                AbstractAircraft newAircraft = null;
                int realSpeedX = RandomGenerator.nonZeroGenerator(Kinematics.bossSpeedX);

                if (score - lastTimeBossSpawned > Difficulty.bossScoreThreshold) {
                    lastTimeBossSpawned = score;
                    if (!BossEnemy.isBossActive()) {
                        newAircraft = BossEnemy.summonBoss((int) (Math.random() * (Graphics.screenWidth - 200)),
                                Kinematics.getRealPixel(Kinematics.bossLocationY),
                                Kinematics.getRealPixel(realSpeedX),
                                0,
                                AircraftHP.bossEnemyHP);
                        newAircraft.setId(id);
                        enemyAircraft.add(newAircraft);
                        // mob = 2 means boss
                        WSService.getClient().send("{\"type\": \"npc_upload\", \"mob\":" + 2 + ", \"id\" : " + id + " , \"location_x\": " +
                                (int) (newAircraft.getLocationX() / Graphics.pixelScalingFactor) + ", \"location_y\": " +
                                (int) (newAircraft.getLocationY() / Graphics.pixelScalingFactor) + ", \"speed_x\": " +
                                realSpeedX + ", \"speed_y\":" +
                                0 + ", \"hp\":" + AircraftHP.bossEnemyHP + "}"
                        );

                        // Increase boss HP
                        AircraftHP.bossEnemyHP += Difficulty.bossHpIncrease;
                        if (Difficulty.difficulty == 2) {
                            System.out.printf("Boss generated. Next HP: %d\n", AircraftHP.bossEnemyHP);
                        }
                        id++;
                    }

                }

            }
        }
    }

    private void spawnNPCandUpload() {
        if (Multiple.isHost) {
            // Host generates enemy aircraft and uploads.
            if (enemyAircraft.size() < Difficulty.enemyMaxNumber) {
                AircraftFactory newAircraftFactory;
                AbstractAircraft newAircraft = null;
                int speedX;
                int hp;
                // Decide which type of enemy should be spawned
                if (Math.random() < Probability.eliteProbability) {
                    newAircraftFactory = new EliteEnemyFactory();
                    int realSpeedX = RandomGenerator.nonZeroGenerator(Kinematics.enemySpeedX);
                    speedX = Kinematics.getRealPixel(realSpeedX);
                    hp = AircraftHP.eliteEnemyHP;
                    newAircraft = newAircraftFactory.createAircraft(
                            (int) (Math.random() * (Graphics.screenWidth - ImageManager.ELITE_IMG.getWidth())),
                            (int) (Math.random() * Graphics.screenHeight * 0.2),
                            speedX,
                            Kinematics.getRealPixel(Kinematics.enemySpeedY),
                            hp
                    );
                    newAircraft.setId(id);
                    enemyAircraft.add(newAircraft);
                    // mob = 1 means elite
                    WSService.getClient().send("{\"type\": \"npc_upload\", \"mob\":" + 1 + ", \"id\" : " + id + " , \"location_x\": " +
                            (int) (newAircraft.getLocationX() / Graphics.pixelScalingFactor) + ", \"location_y\": " +
                            (int) (newAircraft.getLocationY() / Graphics.pixelScalingFactor) + ", \"speed_x\": " +
                            realSpeedX + ", \"speed_y\":" +
                            Kinematics.enemySpeedY + ", \"hp\":" + hp + "}"
                    );
                    id++;

                } else {
                    newAircraftFactory = new MobEnemyFactory();
                    speedX = 0;
                    hp = AircraftHP.mobEnemyHP;

                    newAircraft = newAircraftFactory.createAircraft(
                            (int) (Math.random() * (Graphics.screenWidth - ImageManager.MOB_IMG.getWidth())),
                            (int) (Math.random() * Graphics.screenHeight * 0.2),
                            speedX,
                            Kinematics.getRealPixel(Kinematics.enemySpeedY),
                            hp
                    );
                    newAircraft.setId(id);
                    enemyAircraft.add(newAircraft);
                    // mob = 0 means mob
                    WSService.getClient().send("{\"type\": \"npc_upload\", \"mob\":" + 0 + ", \"id\" : " + id + " , \"location_x\": " +
                            (int) (newAircraft.getLocationX() / Graphics.pixelScalingFactor) + ", \"location_y\": " +
                            (int) (newAircraft.getLocationY() / Graphics.pixelScalingFactor) + ", \"speed_x\": " +
                            (int) (newAircraft.getSpeedX() / Graphics.pixelScalingFactor) + ", \"speed_y\":" +
                            Kinematics.enemySpeedY + ", \"hp\":" + hp + "}"
                    );
                    id++;
                }
            }
        }
    }

    private void uploadEnemyOutScreen() {
        if (Multiple.isHost) {
            for (AbstractAircraft abstractAircraft : enemyAircraft) {
                if (abstractAircraft.getLocationY() >= Graphics.screenHeight) {
                    WSService.getClient().send("{\"type\": \"remove_aircraft\",\"remove\":" + abstractAircraft.getId() + "}");
                }
            }
        }
    }


    private void uploadHeroMovement() {
        // Send movement
        // ??????????????????????????????????????????????????????????????????????????????
        WSService.getClient().send("{\"type\": \"movement\", \"new_x\": " + (int) (HeroAircraft.getInstance().getLocationX() / Graphics.pixelScalingFactor) +
                ", \"new_y\": " + (int) (HeroAircraft.getInstance().getLocationY() / Graphics.pixelScalingFactor) + "}");
    }

    private void fetchMessages() {
        while (!MessageQueue.isEmpty()) {
            JSONObject msg = MessageQueue.poll();
            switch (msg.getString("type")) {

                /*
                 *  ????????? ?????? ????????? case ?????? break
                 *  ????????????
                 */

                case "teammate_movement":
                    teammateMovement(msg);
                    break;
                case "npc_spawn":
                    NPCSpawn(msg);
                    break;
                case "score":
                    addScore(msg);
                    break;
                case "prop_spawn":
                    propSpawn(msg);
                    break;
                case "blood_action":
                    bloodAction();
                    break;
                case "bomb_action":
                    bombAction(msg);
                    break;
                case "bullet_action":
                    bulletAction(msg);
                    break;
                case "game_end":
                    gameEnd(msg);
                    break;
                default:
                    break;
            }
        }
    }

    private void bulletAction(JSONObject msg) {
        SoundHelper.playGetSupply();

        // If target is true, player pick up bullet prop.
        // If target is false, friend pick up bullet prop.
        if (msg.getBooleanValue("target")) {
            HeroAircraft heroAircraft = HeroAircraft.getInstance();
            // add more bullets
            heroAircraft.cannon.setCount(heroAircraft.cannon.getCount() + Difficulty.bulletPropEffectLevel);
            heroAircraft.cannon.setStrategy(new BulletStrategyScatter());
            // increase strategy level
            playerStrategyLevel++;
            Runnable r = () -> {
                try {
                    // sleep for a period of time
                    Thread.sleep(Difficulty.bulletPropEffectTime);
                    // recover
                    heroAircraft.cannon.setCount(heroAircraft.cannon.getCount() - Difficulty.bulletPropEffectLevel);
                    playerStrategyLevel--;
                    if (playerStrategyLevel == 0) {
                        // prevent when multiple bullet props are active,
                        // the first one may erase others' effect.
                        heroAircraft.cannon.setStrategy(new BulletStrategyParallel());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            new Thread(r, "PlayerBulletPropTimer").start();
        } else {
            FriendAircraft friendAircraft = FriendAircraft.getInstance();
            // add more bullets
            friendAircraft.cannon.setCount(friendAircraft.cannon.getCount() + Difficulty.bulletPropEffectLevel);
            friendAircraft.cannon.setStrategy(new BulletStrategyScatter());
            // increase strategy level
            friendStrategyLevel++;

            Runnable r = () -> {
                try {
                    // sleep for a period of time
                    Thread.sleep(Difficulty.bulletPropEffectTime);
                    // recover
                    friendAircraft.cannon.setCount(friendAircraft.cannon.getCount() - Difficulty.bulletPropEffectLevel);
                    friendStrategyLevel--;
                    if (friendStrategyLevel == 0) {
                        // prevent when multiple bullet props are active,
                        // the first one may erase others' effect.
                        friendAircraft.cannon.setStrategy(new BulletStrategyParallel());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            };
            new Thread(r, "FriendBulletPropTimer").start();
        }
    }

    private void bombAction(JSONObject msg) {
        // 1. clear enemy aircraft
        for (AbstractAircraft enemy : enemyAircraft) {
            if (enemy instanceof BossEnemy) {
                continue;
            }
            enemy.vanish();
        }
        // 2. clear enemy bullets
        for (AbstractBullet enemyBullet : enemyBullets) {
            enemyBullet.vanish();
        }
        // 3. add score
        score += msg.getInteger("add_score");
        // 4. play bomb explosion sound
        SoundHelper.playBombExplosion();
    }

    private void bloodAction() {
        SoundHelper.playGetSupply();
        HeroAircraft.getInstance().increaseHp(Difficulty.bloodPropEffectLevel);
    }

    private void propSpawn(JSONObject msg) {
        AbstractProp prop;
        PropFactory newPropFactory = null;

        JSONArray propArray = msg.getJSONArray("props");
        for (int i = 0; i < propArray.size(); i++) {
            JSONObject object = propArray.getJSONObject(i);
            int propKind = object.getInteger("kind");

            switch (propKind) {
                case 0:
                    newPropFactory = new BloodPropFactory();
                    break;
                case 1:
                    newPropFactory = new BombPropFactory();
                    break;
                case 2:
                    newPropFactory = new BulletPropFactory();
                    break;
                default:
                    break;
            }
            prop = newPropFactory.createProp(
                    (int) (object.getInteger("location_x") * Graphics.pixelScalingFactor),
                    (int) (object.getInteger("location_y") * Graphics.pixelScalingFactor),
                    0,
                    (int) (Kinematics.enemySpeedY * Graphics.pixelScalingFactor)
            );
            prop.setId(object.getInteger("id"));
            props.add(prop);
        }
    }

    private void addScore(JSONObject msg) {
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            if (enemyAircraft.getId() == msg.getInteger("remove")) {
                enemyAircraft.vanish();
            }
        }
        score += msg.getInteger("score");
    }

    private void NPCSpawn(JSONObject msg) {
        int mob = msg.getInteger("mob");

        if (mob == 2) {
            // BOSS
            AbstractAircraft newAircraft;
            newAircraft = BossEnemy.summonBoss(
                    (int) (msg.getInteger("location_x") * Graphics.pixelScalingFactor),
                    (int) (msg.getInteger("location_y") * Graphics.pixelScalingFactor),
                    (int) (msg.getInteger("speed_x") * Graphics.pixelScalingFactor),
                    (int) (msg.getInteger("speed_y") * Graphics.pixelScalingFactor),
                    msg.getInteger("hp")
            );
            newAircraft.setId(msg.getInteger("id"));
            enemyAircraft.add(newAircraft);
        } else {
            // Use factory
            AircraftFactory factory = null;
            switch (mob) {
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
                    (int) (msg.getInteger("location_x") * Graphics.pixelScalingFactor),
                    (int) (msg.getInteger("location_y") * Graphics.pixelScalingFactor),
                    (int) (msg.getInteger("speed_x") * Graphics.pixelScalingFactor),
                    (int) (msg.getInteger("speed_y") * Graphics.pixelScalingFactor),
                    msg.getInteger("hp")
            );
            newAircraft.setId(msg.getInteger("id"));
            enemyAircraft.add(newAircraft);
        }
    }

    private void teammateMovement(JSONObject msg) {
        FriendAircraft.setLocation((int) (msg.getInteger("new_x") * Graphics.pixelScalingFactor),
                (int) (msg.getInteger("new_y") * Graphics.pixelScalingFactor));
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
        for (AbstractBullet bullet : friendBullets) {
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


    /**
     * Hit box detection
     */
    private void crashCheckAction() {
        // Enemy bullets
        for (AbstractBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (HeroAircraft.getInstance().crash(bullet)) {
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
                    bullet.vanish();
                    WSService.getClient().send("{ \"type\" : \"damage\", \"id\":" + enemyAircraft.getId() +
                            ", \"hp_decrease\":" + Difficulty.heroBulletPower +
                            ", \"location_x\":" + (int) (enemyAircraft.getLocationX() / Graphics.pixelScalingFactor) +
                            ", \"location_y\":" + (int) (enemyAircraft.getLocationY() / Graphics.pixelScalingFactor) +
                            "}");
                }
            }
        }

        // Friend bullets
        for (AbstractBullet bullet : friendBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircraft) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    SoundHelper.playBulletHit();
                    bullet.vanish();
                }
            }
        }

        // Player pick up prop
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (HeroAircraft.getInstance().crash(prop)) {
                prop.vanish();
                WSService.getClient().send("{ \"type\" : \"prop_action\", \"id\":" + prop.getId() + "}");
            }
        }

        // Friend pick up prop
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (FriendAircraft.getInstance().crash(prop)) {
                SoundHelper.playGetSupply();
                prop.vanish();
            }
        }

        // Enemy and Hero crash
        for (AbstractAircraft enemyAircraft : enemyAircraft) {
            if (enemyAircraft.crash(HeroAircraft.getInstance()) || HeroAircraft.getInstance().crash(enemyAircraft)) {
                enemyAircraft.vanish();
                HeroAircraft.getInstance().decreaseHp(Integer.MAX_VALUE);
            }
        }
    }

    /**
     * Post process
     * - Remove invalid enemies
     * - Remove invalid bullets
     * - Remove invalid & used props
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
                if (Kinematics.enemySpeedY <= Kinematics.enemySpeedYMax) {
                    Kinematics.enemySpeedY += Difficulty.enemySpeedYIncrease;
                }
                System.out.printf("Boss HP: %d, Boss score threshold: %d, Elite probability: %f, Enemy Speed Y: %d\n",
                        AircraftHP.bossEnemyHP, Difficulty.bossScoreThreshold, Probability.eliteProbability, Kinematics.enemySpeedY);
            }
        }
    }

    private void gameOverCheck() {
        if (HeroAircraft.getInstance().getHp() <= 0) {
            // ????????????????????????????????????
            WSService.getClient().send("{\"type\": \"game_end_request\", \"reason\": 0 }");
        }
    }

    private int friendScore = 0;

    private void gameEnd(JSONObject msg) {
        score = msg.getInteger("this_score");
        friendScore = msg.getInteger("teammate_score");
        gameOver = true;
    }

    public int getFriendScore() {
        return friendScore;
    }

    public boolean getGameOver() {
        return gameOver;
    }


}
