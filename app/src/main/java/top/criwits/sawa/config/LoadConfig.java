//package top.criwits.sawa.config;
//
//import top.criwits.sawa.application.GameEasy;
//import top.criwits.sawa.application.GameNorm;
//import top.criwits.sawa.application.ImageManager;
//
///**
// * Config loader
// */
//public class LoadConfig {
//    public static void loadEasyMode() {
//        Difficulty.difficulty = 0;
//        Difficulty.game = new GameEasy();
//        Media.backgroudImage = (Math.random() > 0.5) ?
//                ImageManager.BACKGROUND_IMAGE :
//                ImageManager.BACKGROUND_IMAGE_2;
//
//    }
//
//    public static void loadModerateMode() {
//        Difficulty.difficulty = 1;
//        Difficulty.game = new GameNorm();
//        Media.backgroudImage = (Math.random() > 0.5) ?
//                ImageManager.BACKGROUND_IMAGE_3 :
//                ImageManager.BACKGROUND_IMAGE_4;
//        Difficulty.enemyMaxNumber = 8;
//        Difficulty.enemyBulletPower = 30;
//        Difficulty.bossBulletPower = 45;
//        Kinematics.enemySpeedY = 12;
//    }
//
//    public static void loadHardMode() {
//        Difficulty.difficulty = 2;
//        Difficulty.game = new GameNorm();
//        Media.backgroudImage = ImageManager.BACKGROUND_IMAGE_5;
//        Difficulty.bossHpIncrease = 20;
//        Difficulty.enemyMaxNumber = 10;
//        Difficulty.enemyBulletPower = 35;
//        Difficulty.bossBulletPower = 50;
//        Difficulty.enemySpeedYIncrease = 1;
//        Kinematics.enemySpeedY = 12;
//    }
//}
