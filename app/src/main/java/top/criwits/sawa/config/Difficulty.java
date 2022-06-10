package top.criwits.sawa.config;

/**
 * Params for difficulty control
 */
public class Difficulty {
    // 0 - easy, 1 - moderate, 2 - hard
    public static int difficulty = 0;

    public static int enemyMaxNumber = 5;
    public static int bossScoreThreshold = 200;
    public static int bossPropCount = 3;
    public static int mobEnemyScore = 10;
    public static int eliteEnemyScore = 20;
    public static int bossEnemyScore = 50;

    public static int heroBulletPower = 20;
    public static int enemyBulletPower = 20;
    public static int bossBulletPower = 40;

    public static int bossBulletCount = 4;

    public static int bulletPropEffectTime = 10000;
    public static int bulletPropEffectLevel = 2;

    public static int difficultyIncreaseCycleCount = 200;

    public static int bossHpIncrease = 0;
    public static int bossScoreThresholdDecrease = 5;
    public static int bossScoreThresholdMinimum = 400;
    public static double eliteEnemyProbabilityIncrease = 0.05;
    public static double eliteEnemyProbabilityMaximum = 0.6;
    public static int enemySpeedYIncrease = 0;
}
