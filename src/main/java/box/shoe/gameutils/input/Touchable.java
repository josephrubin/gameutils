package box.shoe.gameutils.input;

import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.HashSet;
import java.util.Set;

public interface Touchable
{
    void processTouch(MotionEvent motionEvent);

    boolean isActive();

    //TODO: change return type? there will be in the future a great RectF AABB mutable/immutable refactoring.
    RectF getBounds();
}
