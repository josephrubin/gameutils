package box.shoe.gameutils;

import android.graphics.PointF;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * Created by Joseph on 10/21/2017.
 * A getMagnitude and a direction in 2D space.
 * This class is IMMUTABLE, so remember to use method return values (the original Vectors will not change).
 * Any method which returns a Vector need not create a new one. If possible, it is able to return one
 * of the arguments itself (e.g. onlyX returns the argument if the argument already has Y == 0).
 * This class only has float precision.
 */

public final class Vector //TODO: could change to double precision and cast to float when returning, but that may be unnecessary.
{
    // Length in the +x (rightward) direction.
    private final float X;
    // Length in the +y (downward) direction.
    private final float Y;

    // The zero vector.
    public static final Vector ZERO = Vector.fromCartesian(0, 0);

    // Unit cardinal directions.
    public static final Vector WEST = Vector.fromCartesian(-1, 0);
    public static final Vector NORTH = Vector.fromCartesian(0, -1);
    public static final Vector EAST = Vector.fromCartesian(1, 0);
    public static final Vector SOUTH = Vector.fromCartesian(0, 1);

    // Unit ordinal directions.
    public static final Vector NORTH_WEST = NORTH.add(WEST).unit();
    public static final Vector NORTH_EAST = NORTH.add(EAST).unit();
    public static final Vector SOUTH_WEST = SOUTH.add(WEST).unit();
    public static final Vector SOUTH_EAST = SOUTH.add(EAST).unit();

    /**
     * Constructs a new Vector.
     * @param x the X length
     * @param y the Y length
     */
    private Vector(float x, float y)
    {
        this.X = x;
        this.Y = y;
    }

    /**
     * Constructs a Vector after casting the double parameters to floats.
     * @param x the X length
     * @param y the Y length
     */
    private Vector(double x, double y)
    {
        this.X = (float) x;
        this.Y = (float) y;
    }

    /**
     * Constructs a Vector from cartesian coordinate values.
     * @param x the X length
     * @param y the Y length
     * @return a Vector with the specified X and Y values.
     */
    @NonNull
    public static Vector fromCartesian(float x, float y)
    {
        return new Vector(x, y);
    }

    /**
     * Constructs a Vector from cartesian coordinate values.
     * @param x the X length (will be casted to float)
     * @param y the Y length (will be casted to float)
     * @return a Vector with the specified X and Y values, after casting them to float.
     */
    @NonNull
    public static Vector fromCartesian(double x, double y)
    {
        return new Vector(x, y);
    }

    /**
     * Constructs a Vector from polar coordinate values.
     * @param magnitude the length
     * @param thetaRadians the angle in radians
     * @return a Vector from the specified getMagnitude and theta from the origin after converting to X, Y values (atCCW from +x).
     */
    @NonNull
    public static Vector fromPolar(float magnitude, float thetaRadians)
    {
        return new Vector(magnitude * Math.cos(thetaRadians), magnitude * Math.sin(thetaRadians));
    }

    /**
     * Constructs a Vector from polar coordinate values.
     * @param magnitude the length (will be casted to float)
     * @param thetaRadians the angle in radians (will be casted to float)
     * @return a Vector from the specified getMagnitude and theta from the origin (CCW from +x).
     */
    @NonNull
    public static Vector fromPolar(double magnitude, double thetaRadians)
    {
        return new Vector(magnitude * Math.cos(thetaRadians), magnitude * Math.sin(thetaRadians));
    }

    /**
     * Construct a Vector from one PointF to another PointF.
     * @param from the starting PointF.
     * @param to the ending PointF.
     * @return a Vector from the first PointF to the second PointF.
     */
    @NonNull
    public static Vector to(PointF from, PointF to)
    {
        return Vector.fromCartesian(to.x - from.x, to.y - from.y);
    }

    /**
     * Calculates the getMagnitude (length) of this Vector
     * @return the Vector's getMagnitude
     */
    public double getMagnitude()
    {
        // Equivalent of Math.sqrt(this.dot(this))
        return Math.sqrt((X * X) + (Y * Y));
    }

    public double getTheta()
    {
        return Math.atan2(Y, X);
    }


    /**
     * Constructs a Vector in the same direction as this one, but with a getMagnitude of 1.
     * @return this Vector's unit Vector.
     */
    @CheckResult
    @NonNull
    public Vector unit()
    {
        double magnitude = getMagnitude();
        // Since we cannot scale by 1/0 we must throw an exception when this.equals(Vector.ZERO).
        if (magnitude == 0)
        {
            throw new ArithmeticException("Vector getMagnitude is 0. Cannot create unit vector.");
        }
        return this.scale(1 / magnitude);
    }

    /**
     * Adds another Vector to this one.
     * @param otherVector the Vector to add to this one.
     * @return a Vector representing the addition of the two supplied Vectors.
     */
    @CheckResult
    @NonNull
    public Vector add(@NonNull Vector otherVector)
    {
        if (this.equals(ZERO)) return otherVector;
        if (otherVector.equals(ZERO)) return this;
        return new Vector(X + otherVector.getX(), Y + otherVector.getY());
    }

    /**
     * Subtracts another Vector from this Vector
     * @param otherVector the Vector to subtract.
     * @return a Vector representing the subtraction of the other Vector from this one.
     */
    @CheckResult
    @NonNull
    public Vector subtract(@NonNull Vector otherVector)
    {
        if (otherVector.equals(ZERO)) return this;
        return new Vector(X - otherVector.getX(), Y - otherVector.getY());
    }

    /**
     * Scales this vector's getMagnitude by a factor.
     * @param factor the factor to scale by.
     * @return a new, scaled Vector.
     */
    @CheckResult
    @NonNull
    public Vector scale(double factor)
    {
        if (factor == 1) return this;
        return new Vector(X * factor, Y * factor);
    }

    /**
     * Takes the dot product of two Vectors.
     * @param otherVector the Vector to multiply with.
     * @return the dot product of the two Vectors.
     */
    @Contract(pure = true)
    public double dot(@NonNull Vector otherVector)
    {
        return (X * otherVector.X) + (Y * otherVector.Y);
    }

    /**
     * Projects this Vector onto another Vector.
     * @param target the Vector to project onto.
     * @return the Vector projection.
     */
    @CheckResult
    @NonNull
    public Vector projectOnto(@NonNull Vector target)
    {
        return target.unit().scale(this.dot(target.unit()));
    }

    /**
     * Returns a Vector with only the X length of this one, and Y set to 0.
     * @return the horizontal Vector.
     */
    @CheckResult
    @NonNull
    public Vector onlyX()
    {
        if (Y == 0) return this;
        return new Vector(X, 0);
    }

    /**
     * Returns a Vector with only the Y length of this one, and X set to 0.
     * @return the vertical Vector.
     */
    @CheckResult
    @NonNull
    public Vector onlyY()
    {
        if (X == 0) return this;
        return new Vector(0, Y);
    }

    /**
     * Rotates this Vector by a number of radians to create a new Vector.
     * @param deltaThetaRadians the radians to rotate by.
     * @return the rotated Vector.
     */
    @CheckResult
    @NonNull
    public Vector rotateBy(double deltaThetaRadians)
    {
        return new Vector(X * Math.cos(deltaThetaRadians) - Y * Math.sin(deltaThetaRadians), X * Math.sin(deltaThetaRadians) + Y * Math.cos(deltaThetaRadians));
    }

    /**
     * Creates a Vector perpendicular to this one.
     * @return the perpendicular Vector.
     */
    @CheckResult
    @NonNull
    public Vector perpendicular()
    {
        return this.rotateBy(Math.PI / 2);
    }

    /**
     * Get the X length of this Vector.
     * @return the X length.
     */
    @Contract(pure = true)
    public float getX()
    {
        return X;
    }

    /**
     * Get the Y length of this Vector.
     * @return the Y length.
     */
    @Contract(pure = true)
    public float getY()
    {
        return Y;
    }

    @NonNull
    @Contract(pure = true)
    @Override
    public String toString()
    {
        return "Vector{" +
                "X=" + X +
                ", Y=" + Y +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Vector vector = (Vector) o;
        return Float.compare(vector.X, X) == 0 && Float.compare(vector.Y, Y) == 0;
    }

    @Override
    public int hashCode()
    {
        int result = (X != +0.0f ? Float.floatToIntBits(X) : 0);
        result = 31 * result + (Y != +0.0f ? Float.floatToIntBits(Y) : 0);
        return result;
    }
}
