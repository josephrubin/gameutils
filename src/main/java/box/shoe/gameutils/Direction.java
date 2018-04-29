package box.shoe.gameutils;

import java.util.HashMap;
import java.util.Map;
//TODO: both ordinal and cardinal directions, and fromTheta should more accurately say what it does (closest existing Direction)
public class Direction
{
    public static final Direction WEST = new Direction(Vector.WEST);
    public static final Direction NORTH = new Direction(Vector.NORTH);
    public static final Direction EAST = new Direction(Vector.EAST);
    public static final Direction SOUTH = new Direction(Vector.SOUTH);

    public final Vector VECTOR;

    private Direction(Vector pointingVector)
    {
        VECTOR = pointingVector;
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