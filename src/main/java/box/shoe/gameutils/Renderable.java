package box.shoe.gameutils;

import android.content.res.Resources;
import android.graphics.Canvas;

/**
 * Something whose state can be visualized.
 */

public interface Renderable
{
    /**
     * Visually represent the state of this Renderable on the Canvas.
     * @param resources to use e.g. to get color constants.
     * @param canvas the Canvas to render to.
     */
    void render(Resources resources, Canvas canvas);
}
