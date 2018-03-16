package box.shoe.gameutils;

import android.content.res.Resources;
import android.graphics.Canvas;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Joseph on 2/13/2018.
 * @deprecated
 */

public class Scene implements Updatable, Renderable
{
    private Collection<Entity> children;

    public Scene()
    {
        children = new LinkedList<>();
    }

    public void addChild(Entity entity)
    {
        children.add(entity);
    }

    public void removeChild(Entity entity)
    {
        children.remove(entity);
    }

    @Override
    public void update()
    {
        CollectionUtils.updateAll(children);
    }

    @Override
    public void render(Resources resources, Canvas canvas)
    {
        ;//CollectionUtils.renderAll(children, resources, canvas);
    }
}
