package top.criwits.sawa.config;

/**
 * Locations and speeds
 */
public class Kinematics {
    public static int enemySpeedY = 5;
    public static int enemySpeedYMax = 9;
    public static int enemySpeedX = 2;
    public static int bossSpeedX = 3;

    public static int bossLocationY = 50;
    public static int backgroundShiftPerFrame = 4;

    public static int getRealPixel(int x) {
        return (int)(Graphics.pixelScalingFactor * x);
    }
}
