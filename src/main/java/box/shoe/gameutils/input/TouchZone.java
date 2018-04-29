package box.shoe.gameutils.input;

import android.graphics.RectF;
import android.support.annotation.CallSuper;
import android.support.annotation.RestrictTo;
import android.view.MotionEvent;

import java.util.LinkedList;
import java.util.List;

import static android.view.View.NO_ID;

/**
 * Created by Joseph on 3/16/2018.
 * A zone on the screen which can be asked if a pointer is currently down within it.
 * All touch related MotionEvents must be passed via processTouch(MotionEvent) in order for it to work.
 */

public class TouchZone implements Touchable
{
    // The zone of the screen where this TouchZone is.
    private RectF bounds;

    private boolean isCircle;

    private ExclusivePointerMode exclusivePointerMode;

    private List<Integer> activePointerIds;
    private int exclusiveActivePointerId;

    public TouchZone(RectF bounds)
    {
        this(bounds, false, ExclusivePointerMode.FIRST);
    }

    public TouchZone(RectF bounds, boolean isCircle)
    {
        this(bounds, isCircle, ExclusivePointerMode.FIRST);
    }

    public TouchZone(RectF bounds, boolean isCircle, ExclusivePointerMode exclusivePointerMode)
    {
        this.bounds = bounds;
        this.isCircle = isCircle;
        activePointerIds = new LinkedList<>();
        this.exclusivePointerMode = exclusivePointerMode;
    }

    public boolean isActive()
    {
        return !activePointerIds.isEmpty();
    }

    public boolean isActivePointerId(int id)
    {
        return activePointerIds.contains(id);
    }

    public int getExclusiveActivePointerId()
    {
        return exclusiveActivePointerId;
    }

    public void processTouch(MotionEvent motionEvent)
    {
        // We are not touched unless we find a pointer within our area.
        activePointerIds.clear();
        exclusiveActivePointerId = NO_ID;

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
                    addActivePointerId(motionEvent.getPointerId(i));
                }
            }
        }
    }

    @CallSuper
    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected void addActivePointerId(int id)
    {
        if (exclusivePointerMode.equals(ExclusivePointerMode.FIRST))
        {
            if (activePointerIds.isEmpty())
            {
                exclusiveActivePointerId = id;
            }
        }
        else if (exclusivePointerMode.equals(ExclusivePointerMode.LAST))
        {
            exclusiveActivePointerId = id;
        }
        activePointerIds.add(id);
    }

    protected boolean withinBounds(float x, float y)
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

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected RectF getBounds()
    {
        return new RectF(bounds);
    }

    public static class ExclusivePointerMode
    {
        public static final ExclusivePointerMode FIRST = new ExclusivePointerMode();
        public static final ExclusivePointerMode LAST = new ExclusivePointerMode();
    }
}
