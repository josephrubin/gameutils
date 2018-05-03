package box.shoe.gameutils.camera;

import android.graphics.Rect;
import android.support.annotation.NonNull;

import box.shoe.gameutils.Entity;

public class SectionCamera extends FollowCamera
{
    float offsetX;
    float offsetY;

    public SectionCamera(@NonNull Entity target, float gamePortionToShowWidth, float gamePortionToShowHeight, Rect fitToVisibleBounds)
    {
        this(target, 0, 0, gamePortionToShowWidth, gamePortionToShowHeight, fitToVisibleBounds);
    }

    public SectionCamera(@NonNull Entity target, float offsetX, float offsetY, float gamePortionToShowWidth, float gamePortionToShowHeight, Rect fitToVisibleBounds)
    {
        super(target, gamePortionToShowWidth, gamePortionToShowHeight, fitToVisibleBounds);
    }

    @Override
    protected void followTarget()
    {
        gamePortionToShow.offsetTo(
                offsetX + ((int) ((target.body.left - offsetX) / gamePortionToShow.width()) * gamePortionToShow.width()),
                offsetY + ((int) ((target.body.top - offsetY) / gamePortionToShow.height()) * gamePortionToShow.height())
        );
    }
}
