package box.shoe.gameutils.input;

import android.view.MotionEvent;

import java.util.HashSet;
import java.util.Set;

public final class TouchConstants
{
    public static final Set<Integer> UP_ACTIONS = new HashSet<>();
    public static final Set<Integer> DOWN_ACTIONS = new HashSet<>();
    public static final Set<Integer> MOVE_ACTIONS = new HashSet<>();
    static
    {
        UP_ACTIONS.add(MotionEvent.ACTION_UP);
        UP_ACTIONS.add(MotionEvent.ACTION_POINTER_UP);

        DOWN_ACTIONS.add(MotionEvent.ACTION_DOWN);
        DOWN_ACTIONS.add(MotionEvent.ACTION_POINTER_DOWN);

        MOVE_ACTIONS.add(MotionEvent.ACTION_MOVE);
    }

    private TouchConstants() {}
}
