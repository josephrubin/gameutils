package box.shoe.gameutils.input;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import box.shoe.gameutils.Vector;

/**
 * Created by Joseph on 3/25/2018.
 */

public class Joystick implements Touchable
{
    private static final int NONE = -1;
    private Vector activeTouchVector;

    private TouchArea zone;
    private int activePointerId = NONE;

    public Joystick(RectF touchAreaBounds)
    {
        zone = new TouchArea(touchAreaBounds, true);
    }

    // throw out all Vectors with getMagnitude > 1 for circular joystick.
    public Vector getActiveTouchVector()
    {
        if (!isActive())
        {
            throw new IllegalStateException("Cannot get active touch Vector on an inactive Joystick. Please call " +
                    "isActive() first to see if the user is interacting with this Joystick.");
        }
        return activeTouchVector;
    }

    @Override
    public void processTouch(MotionEvent motionEvent)
    {
        zone.processTouch(motionEvent);

        // First, see if our active pointer is still active.
        // If not, then this Joystick is no longer active.
        boolean activePointerStillExists = false;
        int action = motionEvent.getActionMasked();
        int index = motionEvent.getActionIndex();
        for (int i = 0; i < motionEvent.getPointerCount(); i++)
        {
            if (motionEvent.getPointerId(i) == activePointerId)
            {
                if (i != index ||
                        TouchConstants.DOWN_ACTIONS.contains(action) || TouchConstants.MOVE_ACTIONS.contains(action))
                {
                    activePointerStillExists = true;
                    break;
                }
            }
        }
        if (!activePointerStillExists)
        {
            activePointerId = NONE;
        }

        // If we are not yet active, but our zone is, now we know that we are active.
        // Since we just became active, we get a new active pointer ID.
        if (!isActive() && zone.isActive())
        {
            activePointerId = zone.getActivePointerIds().get(0);
        }

        // Whenever we are active we make an active touch Vector.
        // Even if our zone is not active, we still may be.
        if (isActive())
        {
            generateActiveTouchVector(motionEvent);
        }
    }

    private void generateActiveTouchVector(MotionEvent motionEvent)
    {
        int activePointerIndex = motionEvent.findPointerIndex(activePointerId);
        PointF joystickCenterPoint = new PointF(zone.bounds.centerX(), zone.bounds.centerY());
        PointF inputPoint = new PointF(motionEvent.getX(activePointerIndex), motionEvent.getY(activePointerIndex));

        activeTouchVector = Vector.to(joystickCenterPoint, inputPoint).scale(1 / (zone.bounds.width() / 2));
        // The Joystick is a circle, and touch input is scaled from 0-1 in getMagnitude from the center.
        // The Joystick can still be active when input is outside of the circle, but the getMagnitude
        // should always be capped at 1.
        if (activeTouchVector.getMagnitude() > 1)
        {
            activeTouchVector = activeTouchVector.unit();
        }
    }

    @Override
    public boolean isActive()
    {
        return activePointerId != NONE;
    }

    @Override
    public RectF getBounds()
    {
        return zone.bounds;
    }
}
