package box.shoe.gameutils.ai;

//todo: note that in addition, implementation must have 0-argument constructor.
//todo: note that compiler will indeed keep the implementation because it is references in AI XML.
public interface Condition
{
    boolean check();
}
