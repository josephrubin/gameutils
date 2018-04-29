package box.shoe.gameutils.input;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import box.shoe.gameutils.Vector;

import static android.view.View.NO_ID;

public class SwipeZone implements Touchable
{
    private int exclusiveActivePointerId;

    private TouchZone touchZone;
    private int units;

    private VelocityTracker velocityTracker;
    private boolean dirtyComputeVelocity = true;

    private boolean canSwipe;

    public SwipeZone(RectF touchZoneBounds, int units)
    {
        touchZone = new TouchZone(touchZoneBounds, false);
        velocityTracker = VelocityTracker.obtain();
        velocityTracker.clear();
        this.units = units;
        canSwipe = true;
        exclusiveActivePointerId = NO_ID;
    }

    public Vector getActiveSwipeVelocity()
    {
        if (!isActive())
        {
            throw new IllegalStateException("Cannot get active touch Direction on an inactive SwipeZone. Please call " +
                    "isActive() first to see if the user is interacting with this SwipeZone.");
        }
        if (dirtyComputeVelocity)
        {
            velocityTracker.computeCurrentVelocity(units);
        }
        return Vector.fromCartesian(velocityTracker.getXVelocity(), velocityTracker.getYVelocity());
    }

    /**
     * Make this SwipeZone inactive until a new pointer is swiping.
     */
    public void consumeLastSwipe()
    {
        canSwipe = false;
    }

    @Override
    public void processTouch(MotionEvent motionEvent)
    {
        touchZone.processTouch(motionEvent);
        dirtyComputeVelocity = true;

        // We only release an exclusiveActivePointerId when it is not on the screen anymore.
        boolean exclusiveActivePointerIdExists = false;
        int index = motionEvent.getActionIndex();
        int action = motionEvent.getActionMasked();
        for (int i = 0; i < motionEvent.getPointerCount(); i++)
        {
            if (motionEvent.getPointerId(i) == exclusiveActivePointerId
                    && (i != index || TouchConstants.DOWN_ACTIONS.contains(action) || TouchConstants.MOVE_ACTIONS.contains(action)))
            {
                exclusiveActivePointerIdExists = true;
                break;
            }
        }
        if (!exclusiveActivePointerIdExists)
        {
            // We need a new exclusiveActivePointerId, or NO_ID.
            exclusiveActivePointerId = touchZone.getExclusiveActivePointerId();
            velocityTracker.clear();
            canSwipe = true;
        }

        if (isActive()
                && !(motionEvent.getPointerId(motionEvent.getActionIndex()) == exclusiveActivePointerId && TouchConstants.UP_ACTIONS.contains(motionEvent.getAction())))
        {
            // Create a dummy MotionEvent with only the data of the active pointer, and feed that into the VelocityTracker --
            // so our velocity is calculated only with respect to the active pointer, and not some weird average of all the pointers.
            // (Read: very clever hack!)
            MotionEvent culledMotionEvent = MotionEvent.obtain(motionEvent.getDownTime(), motionEvent.getEventTime(),
                    motionEvent.getActionMasked(), // Using getAction() here will cause a nasty crash (unknown reason).
                    motionEvent.getX(motionEvent.findPointerIndex(exclusiveActivePointerId)),
                    motionEvent.getY(motionEvent.findPointerIndex(exclusiveActivePointerId)),
                    motionEvent.getMetaState());
            velocityTracker.addMovement(culledMotionEvent);
            culledMotionEvent.recycle();
        }
    }

    @Override
    public boolean isActive()
    {
        return canSwipe && exclusiveActivePointerId != NO_ID;
    }

    //TODO: could add finalizer that logs if was not recycled (only when BuildConfig.DEBUG).
    public void recycle()
    {
        velocityTracker.recycle();
    }
}
