package box.shoe.gameutils;

public class Axis
{
    public static final Axis X = new Axis(Direction.EAST, Direction.WEST);
    public static final Axis Y = new Axis(Direction.SOUTH, Direction.NORTH);

    public final Direction POSITIVE_DIRECTION;
    public final Direction NEGATIVE_DIRECTION;

    private Axis(Direction positiveDirection, Direction negativeDirection)
    {
        POSITIVE_DIRECTION = positiveDirection;
        NEGATIVE_DIRECTION = negativeDirection;
    }
}
