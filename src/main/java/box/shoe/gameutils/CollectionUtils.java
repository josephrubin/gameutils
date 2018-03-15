package box.shoe.gameutils;

import android.content.res.Resources;
import android.graphics.Canvas;

import java.util.Collection;

/**
 * Created by Joseph on 3/13/2018.
 */

public class CollectionUtils //TODO: rename, place methods elsewhere
{
    private CollectionUtils()
    {

    }

    public static void updateAll(Collection<? extends Updatable> collection)
    {
        for (Updatable updatable : collection)
        {
            updatable.update();
        }
    }

    public static void renderAll(Collection<? extends Renderable> collection, Resources resources, Canvas canvas)
    {
        for (Renderable renderable : collection)
        {
            renderable.render(resources, canvas);
        }
    }
}
