package box.shoe.gameutils.effects;

import box.shoe.gameutils.Renderable;
import box.shoe.gameutils.Updatable;

/**
 * Created by Joseph on 3/16/2018.
 */

public interface Effect extends Updatable, Renderable
{
    void produce();
}
