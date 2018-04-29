package box.shoe.gameutils;

import java.util.HashMap;
import java.util.Map;
//TODO: both ordinal and cardinal directions, and fromTheta should more accurately say what it does (closest existing Direction)
public class Direction
{
    public static final Direction WEST = new Direction();
    public static final Direction NORTH = new Direction();
    public static final Direction EAST = new Direction();
    public static final Direction SOUTH = new Direction();

    private static final Map<Direction, Vector> DIRECTION_TO_VECTOR = new HashMap<>(4);
    static
    {
        DIRECTION_TO_VECTOR.put(WEST, Vector.WEST);
        DIRECTION_TO_VECTOR.put(NORTH, Vector.NORTH);
        DIRECTION_TO_VECTOR.put(EAST, Vector.EAST);
        DIRECTION_TO_VECTOR.put(SOUTH, Vector.SOUTH);
    }

    public Vector toVector()
    {
        return DIRECTION_TO_VECTOR.get(this);
    }

    //TODO: from Vector, not from Theta
    public static Direction fromTheta(double theta)
    {
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        final double INVERSE_SQRT_2 = Math.cos(Math.PI / 4);
        if (sinTheta > 0)
        {
            if (cosTheta > INVERSE_SQRT_2)
            {
                return EAST;
            }
            else if (cosTheta < -INVERSE_SQRT_2)
            {
                return WEST;
            }
            else
            {
                return SOUTH;
            }
        }
        else
        {
            if (cosTheta > INVERSE_SQRT_2)
            {
                return EAST;
            }
            else if (cosTheta < -INVERSE_SQRT_2)
            {
                return WEST;
            }
            else
            {
                return NORTH;
            }
        }
    }
}