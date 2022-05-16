package top.criwits.sawa.utils;

/**
 * A random speed generator
 * Given 5, we will have a random one in {-5, -4, -3, -2, -1, 1, 2, 3, 4, 5}
 * No zero will be generated.
 */
public class RandomGenerator {
    public static int nonZeroGenerator(int maxAbs) {
        return (int)((Math.random() > 0.5 ? 1 : -1) * (Math.random() * (maxAbs - 1) + 1));
    }
}
