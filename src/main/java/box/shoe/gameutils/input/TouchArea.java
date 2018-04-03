package box.shoe.gameutils.input;

import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Joseph on 3/16/2018.
 * A zone on the screen which can be asked if a pointer is currently down within it.
 * All touch related MotionEvents must be passed via processTouch(MotionEvent) in order for it to work.
 */

public class TouchArea implements Touchable
{
    // The zone of the screen where this TouchArea is.
    protected RectF bounds;

    private boolean isCircle;

    private List<Integer> activePointerIds;

    public TouchArea(RectF bounds)
    {
        this(bounds, false);
    }

    public TouchArea(RectF bounds, boolean isCircle)
    {
        this.isCircle = isCircle;
        this.bounds = new RectF(bounds);
        activePointerIds = new LinkedList<>();
    }

    public boolean isActive()
    {
        return !activePointerIds.isEmpty();
    }

    public List<Integer> getActivePointerIds()
    {
        return activePointerIds;
    }

    public RectF getBounds()
    {
        return new RectF(bounds);
    }

    public void processTouch(MotionEvent motionEvent)
    {
        // We are not touched unless we find a pointer within our area.
        activePointerIds.clear();

        int action = motionEvent.getActionMasked();
        int index = motionEvent.getActionIndex();

        // Check all of the pointers.
        for (int i = 0; i < motionEvent.getPointerCount(); i++)
        {
            // If the action is in our area...
            if (withinBounds(motionEvent.getX(i), motionEvent.getY(i)))
            {
                // ...then we are certainly touched if it is an old pointer,
                // and we may be touched if it is a new pointer, but only on a DOWN or MOVE action.
                if (i != index ||
                        TouchConstants.DOWN_ACTIONS.contains(action) || TouchConstants.MOVE_ACTIONS.contains(action))
                {
                    activePointerIds.add(motionEvent.getPointerId(i));
                }
            }
        }
    }

    private boolean withinBounds(float x, float y)
    {
        if (isCircle)
        {
            return withinBoundsCircle(x, y);
        }
        else
        {
            return withinBoundsRect(x, y);
        }
    }

    private boolean withinBoundsRect(float x, float y)
    {
        return bounds.contains(x, y);
    }

    private boolean withinBoundsCircle(float x, float y)
    {
        boolean withinBoundsAsRect = withinBoundsRect(x, y);
        if (withinBoundsAsRect)
        {
            float relativeX = x - bounds.centerX();
            float relativeY = y - bounds.centerY();
            float radiusSquared = (bounds.width() / 2) * (bounds.width() / 2);
            float distanceSquared = (relativeX * relativeX) + (relativeY * relativeY);
            return distanceSquared < radiusSquared;
        }
        return false;
    }
}
