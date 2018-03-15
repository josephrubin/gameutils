package box.shoe.gameutils.camera;

import android.graphics.Canvas;
import android.graphics.RectF;

import box.shoe.gameutils.AABB;
import box.shoe.gameutils.Entity;

/**
 * Created by Joseph on 2/20/2018.
 */
public class RectCamera implements Camera
{
    private RectF visibleBounds;
    private AABB bounds;
    private float zoomFactor;
    private float zoomPivotX;
    private float zoomPivotY;

    // When set to true, recalculateVisibleBounds will be called following a call to roll.
    private boolean visibleBoundsDirty = true;

    /**
     * Constructs a new rectangular implementation of Camera, with a setZoomFactor of 1 (no setZoomFactor)
     * and the camera's bounds set to (0, 0, 0, 0).
     * @see #setBounds(float, float, float, float)
     * @see #setBounds(RectF)
     */
    public RectCamera()
    {
        bounds = new AABB();
        visibleBounds = new RectF();
        bounds.set(0, 0, 0,0 );
        zoomFactor = 1;
        setZoomPivot(0, 0);
    }

    @Override
    public void roll(Canvas canvas)
    {
        canvas.translate(bounds.left, bounds.top);
        canvas.scale(zoomFactor, zoomFactor, zoomPivotX, zoomPivotY);
        if (visibleBoundsDirty)
        {
            recalculateVisibleBounds();
            visibleBoundsDirty = false;
        }
    }

    public void setZoomPivot(float x, float y)
    {
        zoomPivotX = x;
        zoomPivotY = y;
        visibleBoundsDirty = true;
    }

    public void setZoomFactor(float zoomFactor)
    {
        this.zoomFactor = zoomFactor;
        visibleBoundsDirty = true;
    }

    public float getZoomFactor()
    {
        return zoomFactor;
    }

    public void pan(float dx, float dy)
    {
        this.bounds.offset(dx, dy);
        visibleBoundsDirty = true;
    }

    // represents visible area when setZoomFactor(1)
    public void setBounds(RectF bounds)
    {
        this.bounds.set(bounds);
        visibleBoundsDirty = true;
    }

    public void setBounds(float left, float top, float right, float bottom)
    {
        this.bounds.set(left, top, right, bottom);
        visibleBoundsDirty = true;
    }

    //TODO: must be tested.
    //TODO: must factor in the zoomPivot for this to be correct.
    private void recalculateVisibleBounds()
    {
        float zoomPivotWidthProportion = (zoomPivotX - bounds.left) / bounds.width();
        float zoomPivotHeightProportion = (zoomPivotY - bounds.top) / bounds.height();

        // First set the visible bounds to the center of bounds.
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();
        visibleBounds.set(centerX, centerY, centerX, centerY);

        // Now expand it, factoring in the setZoomFactor level.
        float width = bounds.width() / zoomFactor;
        float height = bounds.height() / zoomFactor;
        visibleBounds.inset(- width / 2, - height / 2);

        // Lastly, fix regarding the setZoomFactor pivot.
        visibleBounds.offsetTo(zoomPivotX - (zoomPivotWidthProportion * visibleBounds.width()),
                zoomPivotY - (zoomPivotHeightProportion * visibleBounds.height()));
    }

    @Override
    public boolean isVisible(Entity entity)
    {
        return entity.display.intersects(visibleBounds);
    }

    // More visibility checks given that we are a rectangle.
    // A maximum of two of these can be true at any particular time (corners)?

    public boolean isPastLeft(Entity entity)
    {
        return entity.display.right < visibleBounds.left;
    }

    public boolean isPastTop(Entity entity)
    {
        return entity.display.bottom < visibleBounds.top;
    }

    public boolean isPastRight(Entity entity)
    {
        return entity.display.left > visibleBounds.right;
    }

    public boolean isPastBottom(Entity entity)
    {
        return entity.display.top > visibleBounds.bottom;
    }
}
