package box.shoe.gameutils.input;

import android.view.MotionEvent;

public interface Touchable
{
    /**
     * Give a new touch-related MotionEvent to this Touchable so that it can update its state based on user input.
     * Many Touchables require all touch-related MotionEvents to function properly,
     * others only need those where a pointer acted on a screen position occupied by this Touchable.
     * @param motionEvent the new touch-related MotionEvent.
     */
    void processTouch(MotionEvent motionEvent);

    /**
     * Whether or not this Touchable has is being interacted with, according to the MotionEvents passed to processTouch.
     * @return true if this Touchable is being interacted with, and false otherwise.
     */
    boolean isActive();
}
