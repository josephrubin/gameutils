package box.shoe.gameutils.input;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import box.shoe.gameutils.Vector;

import static android.view.View.NO_ID;

/**
 * Created by Joseph on 3/25/2018.
 */

public class Joystick implements VectorTouchable
{
    private int exclusiveActivePointerId;

    private Vector activeTouchVector;

    private RectF touchZoneBounds;
    private TouchZone touchZone;

    private boolean considerSpecificPointerId;
    private int specificPointerIdInteractable;

    public Joystick(RectF bounds)
    {
        exclusiveActivePointerId = NO_ID;
        this.touchZoneBounds = bounds;
        considerSpecificPointerId = false;
        touchZone = new TouchZone(bounds, true);
    }

    /*
        Create a joystick only allowing one pointer id to interact with it.
        Note that when pointers go p they lose their id's so this is best for a temporary joystick,
        such as one created by a JoystickZone.
     */
    public Joystick(RectF bounds, int specificPointerIdInteractable)
    {
        exclusiveActivePointerId = NO_ID;
        this.touchZoneBounds = bounds;
        considerSpecificPointerId = true;
        this.specificPointerIdInteractable = specificPointerIdInteractable;
        touchZone = new TouchZone(bounds, true);
    }

    // throw out all Vectors with getMagnitude > 1 for circular joystick.
    @Override
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
        touchZone.processTouch(motionEvent);

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
            if (considerSpecificPointerId)
            {
                if (touchZone.isActivePointerId(specificPointerIdInteractable))
                {
                    exclusiveActivePointerId = specificPointerIdInteractable;
                }
                else
                {
                    exclusiveActivePointerId = NO_ID;
                }
            }
            else
            {
                exclusiveActivePointerId = touchZone.getExclusiveActivePointerId();
            }
        }

        // Whenever we are active we make an active touch Vector.
        // Even if our touchZone is not active, we still may be.
        if (isActive())
        {
            generateActiveTouchVector(motionEvent);
        }
    }

    private void generateActiveTouchVector(MotionEvent motionEvent)
    {
        int exclusiveActivePointerIndex = motionEvent.findPointerIndex(exclusiveActivePointerId);
        PointF joystickCenterPoint = new PointF(touchZoneBounds.centerX(), touchZoneBounds.centerY());
        PointF inputPoint = new PointF(motionEvent.getX(exclusiveActivePointerIndex), motionEvent.getY(exclusiveActivePointerIndex));

        // The Joystick is a circle, and touch input is scaled from 0-1 in magnitude from the center to the circumference.
        // The Joystick can still be active when input is outside of the circle, and the magnitude will be > 1 in such a case.
        activeTouchVector = Vector.to(joystickCenterPoint, inputPoint).scale(1 / (touchZoneBounds.width() / 2));
    }

    @Override
    public boolean isActive()
    {
        return exclusiveActivePointerId != NO_ID;
    }
}
