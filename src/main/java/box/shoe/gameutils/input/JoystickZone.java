package box.shoe.gameutils.input;

import android.graphics.RectF;
import android.view.MotionEvent;

import box.shoe.gameutils.BoundingBox;
import box.shoe.gameutils.Vector;

public class JoystickZone implements VectorTouchable
{
    private TouchZone touchZone;

    private float joystickWidth;
    private float joystickHeight;

    private Joystick currentJoystick;
    private Vector activeTouchVector;
    private BoundingBox currentJoystickBounds;

    public JoystickZone(RectF bounds, float joystickWidth, float joystickHeight)
    {
        this.joystickWidth = joystickWidth;
        this.joystickHeight = joystickHeight;
        touchZone = new TouchZone(bounds);
    }

    @Override
    public void processTouch(MotionEvent motionEvent)
    {
        touchZone.processTouch(motionEvent);
        if (currentJoystick == null && touchZone.isActive())
        {
            if (TouchConstants.DOWN_ACTIONS.contains(motionEvent.getActionMasked()))
            {
                currentJoystickBounds = new BoundingBox(0, 0, joystickWidth, joystickHeight);
                currentJoystickBounds.offsetCenterTo(motionEvent.getX(motionEvent.getActionIndex()), motionEvent.getY(motionEvent.getActionIndex()));
                currentJoystick = new Joystick(currentJoystickBounds, motionEvent.getPointerId(motionEvent.getActionIndex()));
            }
        }

        if (currentJoystick != null)
        {
            currentJoystick.processTouch(motionEvent);

            if (currentJoystick.isActive())
            {
                activeTouchVector = currentJoystick.getActiveTouchVector();
            }
            else
            {
                // If our joystick is no longer active, reset it to null.
                // Once it is gone, a new one will be created on the next touch down.
                currentJoystick = null;
            }
        }
    }

    public RectF getCurrentJoystickBounds()
    {
        if (!isActive())
        {
            throw new IllegalStateException("Cannot get current joystick bounds on an inactive JoystickZone. Please call " +
                    "isActive() first to see if the user is interacting with this JoystickZone.");
        }
        return currentJoystickBounds;
    }

    @Override
    public Vector getActiveTouchVector()
    {
        if (!isActive())
        {
            throw new IllegalStateException("Cannot get active touch Vector on an inactive JoystickZone. Please call " +
                    "isActive() first to see if the user is interacting with this JoystickZone.");
        }
        return activeTouchVector;
    }

    @Override
    public boolean isActive()
    {
        return currentJoystick != null && currentJoystick.isActive();
    }
}
