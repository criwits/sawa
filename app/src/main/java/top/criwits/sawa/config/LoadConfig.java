package top.criwits.sawa.config;

import top.criwits.sawa.utils.ImageManager;

/**
 * Config loader
 */
public class LoadConfig {
    public static void loadEasyMode() {
        Difficulty.difficulty = 0;
        Media.backgroudImage = (Math.random() > 0.5) ?
                ImageManager.BG_IMG :
                ImageManager.BG2_IMG;
        Difficulty.bossHpIncrease = 0;
        Difficulty.enemyMaxNumber = 5;
        Difficulty.enemyBulletPower = 20;
        Difficulty.bossBulletPower = 40;
        Difficulty.enemySpeedYIncrease = 0;
        Kinematics.enemySpeedY = 10;

    }

    public static void loadModerateMode() {
        Difficulty.difficulty = 1;
        Media.backgroudImage = (Math.random() > 0.5) ?
                ImageManager.BG3_IMG :
                ImageManager.BG4_IMG;
        Difficulty.bossHpIncrease = 0;
        Difficulty.enemyMaxNumber = 8;
        Difficulty.enemyBulletPower = 30;
        Difficulty.bossBulletPower = 45;
        Difficulty.enemySpeedYIncrease = 0;
        Kinematics.enemySpeedY = 12;
    }

    public static void loadHardMode() {
        Difficulty.difficulty = 2;
        Media.backgroudImage = ImageManager.BG5_IMG;
        Difficulty.bossHpIncrease = 20;
        Difficulty.enemyMaxNumber = 10;
        Difficulty.enemyBulletPower = 35;
        Difficulty.bossBulletPower = 50;
        Difficulty.enemySpeedYIncrease = 1;
        Kinematics.enemySpeedY = 12;
    }
}
