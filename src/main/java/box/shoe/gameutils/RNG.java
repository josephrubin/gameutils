package box.shoe.gameutils;

import java.util.Random;

/**
 * Created by Joseph on 3/6/2018.
 */

public class RNG
{
    public static final Random RANDOM = new Random();

    /**
     * No instantiation.
     */
    private RNG() {}

    // ___________________
    // ADDITIONAL METHODS.

    public static int intFrom(int min, int max)
    {
        return RANDOM.nextInt(max + 1 - min) + min;
    }

    // _____________________
    // PULL-THROUGH METHODS.

    public static void setSeed(long seed)
    {
        RANDOM.setSeed(seed);
    }

    public static void nextBytes(byte[] bytes)
    {
        RANDOM.nextBytes(bytes);
    }

    public static int nextInt()
    {
        return RANDOM.nextInt();
    }

    public static double nextInt(int bound)
    {
        return RANDOM.nextInt(bound);
    }

    public static double nextDouble()
    {
        return RANDOM.nextDouble();
    }

    public static float nextFloat()
    {
        return RANDOM.nextFloat();
    }

    public static long nextLong()
    {
        return RANDOM.nextLong();
    }

    public static boolean nextBoolean()
    {
        return RANDOM.nextBoolean();
    }

    public static double nextGaussian()
    {
        return RANDOM.nextGaussian();
    }
}
