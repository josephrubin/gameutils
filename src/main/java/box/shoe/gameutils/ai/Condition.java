package box.shoe.gameutils.ai;

import box.shoe.gameutils.Entity;

//todo: note that in addition, implementation must have 0-argument constructor.
//todo: note that compiler will indeed keep the implementation because it is referenced in AI XML.
public interface Condition
{
    boolean check(Entity entity);
}
