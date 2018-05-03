package box.shoe.gameutils.input;

import android.graphics.RectF;
import android.view.MotionEvent;

import box.shoe.gameutils.Updatable;

public class HoldZone implements Touchable, Updatable
{
    private TouchZone touchZone;

    private int touchDurationUpdates;

    private boolean canBeActive;

    public HoldZone(RectF bounds)
    {
        this(bounds, false, TouchZone.ExclusivePointerMode.FIRST);
    }

    public HoldZone(RectF bounds, boolean isCircle)
    {
        this(bounds, isCircle, TouchZone.ExclusivePointerMode.FIRST);
    }

    public HoldZone(RectF bounds, boolean isCircle, TouchZone.ExclusivePointerMode exclusivePointerMode)
    {
        touchDurationUpdates = 0;
        canBeActive = true;
        touchZone = new TouchZone(bounds, isCircle, exclusivePointerMode);
    }

    public int getTouchDurationUpdates()
    {
        if (!isActive())
        {
            throw new IllegalStateException("Cannot get touch duration on an inactive HoldZone. Please call " +
                    "isActive() first to see if the user is interacting with this HoldZone.");
        }
        return touchDurationUpdates;
    }

    public void reset()
    {
        touchDurationUpdates = 0;
    }

    public void resetUntilPointersLeave()
    {
        canBeActive = false;
        reset();
    }

    @Override
    public void processTouch(MotionEvent motionEvent)
    {
        touchZone.processTouch(motionEvent);

        // If the user wanted to be inactive until all pointers leave,
        // then only allow active again once the touch zone is completely inactive.
        if (!touchZone.isActive())
        {
            canBeActive = true;
            touchDurationUpdates = 0;
        }
    }

    @Override
    public void update()
    {
        if (isActive())
        {
            touchDurationUpdates++;
        }
    }

    @Override
    public boolean isActive()
    {
        return canBeActive && touchZone.isActive();
    }
}
