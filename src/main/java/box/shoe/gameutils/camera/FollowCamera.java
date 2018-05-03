package box.shoe.gameutils.camera;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import box.shoe.gameutils.AABB;
import box.shoe.gameutils.Entity;

public class FollowCamera implements Camera
{
    protected Entity target;

    protected AABB gamePortionToShow;
    protected RectCamera rectCamera;

    private RectF outerBounds = null;

    public FollowCamera(@NonNull Entity target, float gamePortionToShowWidth, float gamePortionToShowHeight, Rect fitToVisibleBounds)
    {
        this.target = target;

        gamePortionToShow = new AABB(0, 0, gamePortionToShowWidth, gamePortionToShowHeight);
        gamePortionToShow.offsetCenterTo(this.target.body.centerX(), this.target.body.centerY());
        rectCamera = new RectCamera(gamePortionToShow, fitToVisibleBounds);
    }

    public FollowCamera(@NonNull Entity target, float gamePortionToShowWidth, float gamePortionToShowHeight, RectF outerBounds, Rect fitToVisibleBounds)
    {
        this.target = target;

        this.outerBounds = outerBounds;

        gamePortionToShow = new AABB(0, 0, gamePortionToShowWidth, gamePortionToShowHeight);
        gamePortionToShow.offsetCenterTo(this.target.body.centerX(), this.target.body.centerY());
        rectCamera = new RectCamera(gamePortionToShow, fitToVisibleBounds);
    }

    @Override
    public void roll(Canvas canvas)
    {
        followTarget();

        handleOuterBounds();

        rectCamera.setGamePortionToShow(gamePortionToShow);
        rectCamera.roll(canvas);
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    protected void followTarget()
    {
        gamePortionToShow.offsetCenterTo(target.body.centerX(), target.body.centerY());
    }

    private void handleOuterBounds()
    {
        // Don't go past the outer bounds...
        // Null indicates no outerBounds.
        // If outer bounds are too small, center the camera.
        if (outerBounds != null)
        {
            if (outerBounds.width() < gamePortionToShow.width())
            {
                gamePortionToShow.offsetCenterTo(outerBounds.centerX(), gamePortionToShow.centerY());
            }
            else
            {
                if (gamePortionToShow.left < outerBounds.left)
                {
                    gamePortionToShow.offsetLeftTo(outerBounds.left);
                }
                else if (gamePortionToShow.right > outerBounds.right)
                {
                    gamePortionToShow.offsetRightTo(outerBounds.right);
                }
            }

            if (outerBounds.height() < gamePortionToShow.height())
            {
                gamePortionToShow.offsetCenterTo(gamePortionToShow.centerX(), outerBounds.centerY());
            }
            else
            {
                if (gamePortionToShow.top < outerBounds.top)
                {
                    gamePortionToShow.offsetTopTo(outerBounds.top);
                }
                else if (gamePortionToShow.bottom > outerBounds.bottom)
                {
                    gamePortionToShow.offsetBottomTo(outerBounds.bottom);
                }
            }
        }
    }

    @Override
    public boolean isVisible(Entity entity)
    {
        return rectCamera.isVisible(entity);
    }
}
