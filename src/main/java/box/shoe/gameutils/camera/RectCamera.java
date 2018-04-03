package box.shoe.gameutils.camera;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import box.shoe.gameutils.AABB;
import box.shoe.gameutils.Entity;
import box.shoe.gameutils.Interpolatable;

/**
 * Created by Joseph on 2/20/2018.
 */
public class RectCamera implements Camera, Interpolatable
{
    private RectF gamePortionBounds;
    private AABB visibleBounds;

    private RectF interpolatedGamePortionBounds;
    private AABB interpolatedVisibleBounds;

    public RectCamera(RectF gamePortionToShow, Rect fitToVisibleBounds)
    {
        this.gamePortionBounds = new RectF(gamePortionToShow);
        visibleBounds = new AABB(fitToVisibleBounds);

        interpolatedGamePortionBounds = new RectF(this.gamePortionBounds);
        interpolatedVisibleBounds = new AABB(visibleBounds);

        INTERPOLATABLE_SERVICE.addMember(this);
    }

    public void setGamePortionToShow(RectF gamePortionToShow)
    {
        gamePortionBounds = new RectF(gamePortionToShow);
    }

    public void setVisibleBounds(RectF visibleBounds)
    {
        this.visibleBounds = new AABB(visibleBounds);
    }

    @Override
    public void roll(Canvas canvas)
    {
        float scaledWidth = interpolatedVisibleBounds.width() / interpolatedGamePortionBounds.width();
        float scaledHeight = interpolatedVisibleBounds.height() / interpolatedGamePortionBounds.height();
        canvas.scale(scaledWidth, scaledHeight, interpolatedVisibleBounds.left, interpolatedVisibleBounds.top);
        canvas.translate(interpolatedVisibleBounds.left - interpolatedGamePortionBounds.left,
                interpolatedVisibleBounds.top - interpolatedGamePortionBounds.top);
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

    @Override
    public int getInterpValuesArrayMaxIndex()
    {
        return 7;
    }

    @Override
    public void saveInterpValues(float[] out)
    {
        out[0] = gamePortionBounds.left;
        out[1] = gamePortionBounds.top;
        out[2] = gamePortionBounds.right;
        out[3] = gamePortionBounds.bottom;

        out[4] = visibleBounds.left;
        out[5] = visibleBounds.top;
        out[6] = visibleBounds.right;
        out[7] = visibleBounds.bottom;
    }

    @Override
    public void loadInterpValues(float[] in)
    {
        interpolatedGamePortionBounds.left = in[0];
        interpolatedGamePortionBounds.top = in[1];
        interpolatedGamePortionBounds.right = in[2];
        interpolatedGamePortionBounds.bottom = in[3];

        interpolatedVisibleBounds.left = in[4];
        interpolatedVisibleBounds.top = in[5];
        interpolatedVisibleBounds.right = in[6];
        interpolatedVisibleBounds.bottom = in[7];
    }
}
