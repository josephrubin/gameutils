package box.shoe.gameutils.camera;

import android.graphics.Canvas;

import box.shoe.gameutils.Entity;

/**
 * Created by Joseph on 1/1/2018.
 * Cameras by themselves don't cull Entities for which !isVisible. //TODO: Complete description.
 */
public interface Camera
{
    /**
     * Attaches the camera by preconfiguring the supplied canvas so that renders occur through the lens of the camera.
     * @param canvas the canvas to view through the camera.
     */
    void roll(Canvas canvas); //attach?

    /**
     * Returns true iff the supplied Entity's display bounds indicate that it
     * is seen through this camera. Must give consistent results between calls to roll().
     * @param entity the Entity to check for visibility.
     * @return true if entity is visible, and false otherwise.
     */
    boolean isVisible(Entity entity);
}