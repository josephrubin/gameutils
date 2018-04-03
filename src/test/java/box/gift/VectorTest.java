package box.gift;

import org.junit.Test;

import box.shoe.gameutils.Vector;

import static org.junit.Assert.assertEquals;

/**
 * Created by Joseph on 3/16/2018.
 */

public class VectorTest
{
    private final float DELTA = 0.00000011920928955078125f;

    @Test
    public void fromCartesian()
    {
        Vector test = Vector.fromCartesian(12.4, 15.3);
        assertEquals("X is correct", 12.4f, test.getX(), 0);
        assertEquals("Y is correct", 15.3f, test.getY(), 0);
    }

    @Test
    public void fromPolar()
    {
        Vector test = Vector.fromPolar(2, Math.PI / 2);
        assertEquals("X is correct", 0, test.getX(), DELTA);
        assertEquals("Y is correct", 2, test.getY(), DELTA);
    }

    @Test
    public void add()
    {
        Vector first = Vector.fromCartesian(10, 15);
        Vector second = Vector.fromCartesian(12, 14);
        assertEquals("Vectors add correctly", first.add(second), Vector.fromCartesian(22, 29));
    }

    @Test
    public void subtract()
    {
        Vector first = Vector.fromCartesian(10, 15);
        Vector second = Vector.fromCartesian(12, 14);
        assertEquals("Vectors subtract correctly", first.subtract(second), Vector.fromCartesian(-2, 1));
    }

    @Test
    public void magnitude()
    {
        Vector test = Vector.fromCartesian(3, 4);
        assertEquals("Magnitude calculates correctly", (int) test.getMagnitude(), 5);
    }

    @Test
    public void unit()
    {
        Vector test = Vector.fromCartesian(2, 0).unit();
        assertEquals("Unit Vector calculated correctly", test, Vector.EAST);
    }
}
