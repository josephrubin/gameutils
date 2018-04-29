package box.shoe.gameutils.camera;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import box.shoe.gameutils.AABB;
import box.shoe.gameutils.Entity;

public class FollowCamera implements Camera
{
    private Entity follow;

    private AABB gamePortionToShow;
    private RectCamera rectCamera;

    private RectF outerBounds = null;

    public FollowCamera(@NonNull Entity entityToFollow, float gamePortionToShowWidth, float gamePortionToShowHeight, Rect fitToVisibleBounds)
    {
        follow = entityToFollow;

        gamePortionToShow = new AABB(0, 0, gamePortionToShowWidth, gamePortionToShowHeight);
        gamePortionToShow.offsetCenterTo(follow.body.centerX(), follow.body.centerY());
        rectCamera = new RectCamera(gamePortionToShow, fitToVisibleBounds);
    }

    public FollowCamera(@NonNull Entity entityToFollow, float gamePortionToShowWidth, float gamePortionToShowHeight, RectF outerBounds, Rect fitToVisibleBounds)
    {
        follow = entityToFollow;

        setOuterBounds(outerBounds);

        gamePortionToShow = new AABB(0, 0, gamePortionToShowWidth, gamePortionToShowHeight);
        gamePortionToShow.offsetCenterTo(follow.body.centerX(), follow.body.centerY());
        rectCamera = new RectCamera(gamePortionToShow, fitToVisibleBounds);
    }

    @Override
    public void roll(Canvas canvas)
    {
        // Follow player.
        gamePortionToShow.offsetCenterTo(follow.body.centerX(), follow.body.centerY());

        handleOuterBounds();

        rectCamera.setGamePortionToShow(gamePortionToShow);
        rectCamera.roll(canvas);
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

    // Null indicates no bounds.
    public void setOuterBounds(RectF outerBounds)
    {
        this.outerBounds = new RectF(outerBounds);
    }

    @Override
    public boolean isVisible(Entity entity)
    {
        return rectCamera.isVisible(entity);
    }
}
