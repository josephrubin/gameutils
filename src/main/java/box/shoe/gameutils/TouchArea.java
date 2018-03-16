package box.shoe.gameutils;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Joseph on 3/16/2018.
 */

public class TouchArea
{
    private static final Set<Integer> DOWN_ACTIONS = new HashSet<>();
    private static final Set<Integer> UP_ACTIONS = new HashSet<>();
    private static final Set<Integer> MOVE_ACTIONS = new HashSet<>();
    static
    {
        DOWN_ACTIONS.add(MotionEvent.ACTION_DOWN);
        DOWN_ACTIONS.add(MotionEvent.ACTION_POINTER_DOWN);

        UP_ACTIONS.add(MotionEvent.ACTION_UP);
        UP_ACTIONS.add(MotionEvent.ACTION_POINTER_UP);

        MOVE_ACTIONS.add(MotionEvent.ACTION_MOVE);
    }

    private RectF area;

    private Set<Integer> touchingPointerIds;

    public TouchArea(RectF areaBounds)
    {
        this.area = areaBounds;
        touchingPointerIds = new HashSet<>();
    }

    public boolean isTouched()
    {
        return !touchingPointerIds.isEmpty();
    }

    public void processTouch(MotionEvent motionEvent)
    {
        int action = motionEvent.getActionMasked();
        int index = motionEvent.getActionIndex();
        int id = motionEvent.getPointerId(index);

        if (action == MotionEvent.ACTION_CANCEL)
        {
            if (touchingPointerIds.contains(id))
            {
                touchingPointerIds.remove(id);
            }
        }
        else if (DOWN_ACTIONS.contains(action))
        {
            if (area.contains(motionEvent.getX(index), motionEvent.getY(index)))
            {
                if (!touchingPointerIds.contains(id))
                {
                    touchingPointerIds.add(id);
                }
            }
        }
        else if (UP_ACTIONS.contains(action))
        {
            if (area.contains(motionEvent.getX(index), motionEvent.getY(index)))
            {
                if (touchingPointerIds.contains(id))
                {
                    touchingPointerIds.remove(id);
                }
            }
        }
        else if (MOVE_ACTIONS.contains(action))
        {
            if (area.contains(motionEvent.getX(index), motionEvent.getY(index)))
            {
                if (!touchingPointerIds.contains(id))
                {
                    touchingPointerIds.add(id);
                }
            }
            else
            {
                if (touchingPointerIds.contains(id))
                {
                    touchingPointerIds.remove(id);
                }
            }
        }
    }
}
