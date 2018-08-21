package box.shoe.gameutils;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Joseph on 2/15/2018.
 * Unclear yet what this represents.
 * But for now, the Axis-Aligned-Bonding-Box class serves to add
 * some functionality to a bare RectF.
 */

public class BoundingBox extends RectF
{
    // ___________
    // COLLISIONS.

    public boolean canSee(RectF what, float maxDistance)
    {
        return canSee(what, Direction.WEST, maxDistance)
                || canSee(what, Direction.NORTH, maxDistance)
                || canSee(what, Direction.EAST, maxDistance)
                || canSee(what, Direction.SOUTH, maxDistance);
    }

    public boolean canSee(RectF what, Direction direction, float maxDistance)
    {
        BoundingBox sightBounds = new BoundingBox(centerX(), top, centerX() + maxDistance, bottom);
        sightBounds.rotate(Direction.EAST, direction, centerX(), centerY());
        return intersects(sightBounds);
    }

    /**
     * Returns true if this BoundingBox intersects the specified BoundingBox.
     * In no event is this BoundingBox modified. No check is performed to see
     * if either BoundingBox is empty. To record the intersection, use intersect()
     * or setIntersect().
     *
     * @param other the BoundingBox to check for intersection against.
     * @return true iff the specified BoundingBox intersects this BoundingBox. In
     *              no event is this BoundingBox modified.
     */
    public boolean intersects(RectF other)
    {
        return this.left < other.right && other.left < this.right
                && this.top < other.bottom && other.top < this.bottom;
    }

    public boolean touches(RectF other)
    {
        return
                bottomTouchesTopOf(other)
                || topTouchesBottomOf(other)
                || rightTouchesLeftOf(other)
                || leftTouchesRightOf(other);
    }

    public boolean bottomTouchesTopOf(RectF other)
    {
        return this.left < other.right && other.left < this.right
                && (other.top == this.bottom);
    }

    public boolean topTouchesBottomOf(RectF other)
    {
        return this.left < other.right && other.left < this.right
                && (other.bottom == this.top);
    }

    public boolean leftTouchesRightOf(RectF other)
    {
        return this.top < other.bottom && other.top < this.bottom
                && (other.right == this.left);
    }

    public boolean rightTouchesLeftOf(RectF other)
    {
        return this.top < other.bottom && other.top < this.bottom
                && (other.left == this.right);
    }

    // ________
    // OFFSETS.

    public void offset(Vector offsetVector)
    {
        offset(offsetVector.getX(), offsetVector.getY());
    }

    /**
     * Offset to a specific (top) position,
     * keeping width and height the same.
     *
     * @param newTop    The new "top" coordinate
     */
    public void offsetTopTo(float newTop)
    {
        bottom += newTop - top;
        top = newTop;
    }

    /**
     * Offset to a specific (right) position,
     * keeping width and height the same.
     *
     * @param newRight    The new "right" coordinate
     */
    public void offsetRightTo(float newRight)
    {
        left += newRight - right;
        right = newRight;
    }

    /**
     * Offset to a specific (bottom) position,
     * keeping width and height the same.
     *
     * @param newBottom    The new "bottom" coordinate
     */
    public void offsetBottomTo(float newBottom)
    {
        top += newBottom - bottom;
        bottom = newBottom;
    }

    /**
     * Offset to a specific (left) position,
     * keeping width and height the same.
     *
     * @param newLeft    The new "left" coordinate
     */
    public void offsetLeftTo(float newLeft)
    {
        right += newLeft - left;
        left = newLeft;
    }

    /**
     * Offset to a specific (center) position,
     * keeping width and height the same.
     *
     * @param newCenterX    The new "center" x coordinate
     * @param newCenterY    The new "center" y coordinate
     */
    public void offsetCenterTo(float newCenterX, float newCenterY)
    {
        offsetTo(newCenterX - width() / 2, newCenterY - height() / 2);
    }

    // ________________
    // TRANSFORMATIONS.

    public void rotate(Direction from, Direction to, float pivotX, float pivotY)
    {
        HashMap<Direction, Integer> map = new HashMap<>(4);
        map.put(Direction.WEST, 0);
        map.put(Direction.NORTH, 1);
        map.put(Direction.EAST, 2);
        map.put(Direction.SOUTH, 3);

        rotate(((map.get(to) - map.get(from)) + 4) % 4, pivotX, pivotY);
    }

    public void rotate(int timesClockwise, float pivotX, float pivotY)
    {
        if (timesClockwise < 0 || timesClockwise > 3)
        {
            throw new IllegalArgumentException("timesClockwise must be between 0 and 3");
        }

        left -= pivotX;
        top -= pivotY;
        right -= pivotX;
        bottom -= pivotY;

        float[] dimensions = new float[] {bottom, right, top, left};

        boolean invertX = timesClockwise == 1 || timesClockwise == 2;
        boolean invertY = timesClockwise == 3 || timesClockwise == 2;

        left = dimensions[(3 + timesClockwise) % 4] * (invertX ? -1 : 1);
        top = dimensions[(2 + timesClockwise) % 4] * (invertY ? -1 : 1);
        right = dimensions[(1 + timesClockwise) % 4] * (invertX ? -1 : 1);
        bottom = dimensions[(0 + timesClockwise) % 4] * (invertY ? -1 : 1);

        left += pivotX;
        top += pivotY;
        right += pivotX;
        bottom += pivotY;
    }

    // _____________
    // CONSTRUCTORS.

    /**
     * Create a new empty BoundingBox. All coordinates are initialized to 0.
     */
    public BoundingBox()
    {

    }

    /**
     * Create a new BoundingBox with the specified coordinates. Note: no range
     * checking is performed, so the caller must ensure that left <= right and
     * top <= bottom.
     *
     * @param left   The X coordinate of the left side of the BoundingBox
     * @param top    The Y coordinate of the top of the BoundingBox
     * @param right  The X coordinate of the right side of the BoundingBox
     * @param bottom The Y coordinate of the bottom of the BoundingBox
     */
    public BoundingBox(float left, float top, float right, float bottom)
    {
        super(left, top, right, bottom);
    }

    public BoundingBox(RectF r)
    {
        super(r);
    }

    public BoundingBox(Rect r)
    {
        super(r);
    }

    public static BoundingBox fromCenter(float centerX, float centerY, float edgeLength)
    {
        return BoundingBox.fromCenter(centerX, centerY, edgeLength, edgeLength);
    }

    public static BoundingBox fromCenter(float centerX, float centerY, float width, float height)
    {
        return new BoundingBox(centerX - width / 2, centerY - height / 2, centerX + height / 2, centerY + height / 2);
    }
}
