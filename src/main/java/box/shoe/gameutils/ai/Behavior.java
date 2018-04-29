package box.shoe.gameutils.ai;

import box.shoe.gameutils.Entity;

//todo: note that in addition, implementation must have 0-argument constructor.
//todo: note that compiler will indeed keep the implementation because it is references in AI XML.
public interface Behavior
{
    void enter(Entity entity);

    void behave(Entity entity);

    void exit(Entity entity);
}
