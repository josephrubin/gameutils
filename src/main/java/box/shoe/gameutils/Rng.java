package box.shoe.gameutils;

import java.util.Random;

/**
 * Created by Joseph on 3/6/2018.
 * A single Random instance to be shared among all objects that need Rng.
 * All public methods of java.util.Random are forwarded through,
 * and some additional methods are added, for convenience.
 * For a deterministic game, make sure to call Rng.setSeed(long seed) when the game starts.
 */

public class Rng
{
    private static final Random RANDOM = new Random();

    /**
     * No instantiation.
     */
    private Rng() {}

    // ___________________
    // ADDITIONAL METHODS.

    /**
     * Supplies an int within a range, inclusive.
     * @param min the lowest possible return value.
     * @param max the highest possible return value.
     * @return the random int within the inclusive range.
     */
    public static int intFrom(int min, int max)
    {
        return RANDOM.nextInt(max + 1 - min) + min;
    }

    public static Direction nextDirection()
    {
        switch (intFrom(0, 3))
        {
            case 0:
                return Direction.WEST;
            case 1:
                return Direction.NORTH;
            case 2:
                return Direction.EAST;
            case 3:
                return Direction.SOUTH;
            default:
                throw new Error("Impossible execution: invalid random int generation.");
        }
    }

    // ___________________
    // FORWARDING METHODS.

    /**
     * {@inheritDoc}
     */
    public static void setSeed(long seed)
    {
        RANDOM.setSeed(seed);
    }

    /**
     * {@inheritDoc}
     */
    public static void nextBytes(byte[] bytes)
    {
        RANDOM.nextBytes(bytes);
    }

    /**
     * {@inheritDoc}
     */
    public static int nextInt()
    {
        return RANDOM.nextInt();
    }

    /**
     * {@inheritDoc}
     */
    public static double nextInt(int bound)
    {
        return RANDOM.nextInt(bound);
    }

    /**
     * {@inheritDoc}
     */
    public static double nextDouble()
    {
        return RANDOM.nextDouble();
    }

    /**
     * {@inheritDoc}
     */
    public static float nextFloat()
    {
        return RANDOM.nextFloat();
    }

    /**
     * {@inheritDoc}
     */
    public static long nextLong()
    {
        return RANDOM.nextLong();
    }

    /**
     * {@inheritDoc}
     */
    public static boolean nextBoolean()
    {
        return RANDOM.nextBoolean();
    }

    /**
     * {@inheritDoc}
     */
    public static double nextGaussian()
    {
        return RANDOM.nextGaussian();
    }
}
