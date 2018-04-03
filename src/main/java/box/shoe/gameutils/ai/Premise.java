package box.shoe.gameutils.ai;

import android.support.annotation.NonNull;

/* pack */ class Premise
{
    public final Behavior BEHAVIOR;

    public Premise(@NonNull Behavior behavior)
    {
        BEHAVIOR = behavior;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Premise premise = (Premise) o;

        return BEHAVIOR.equals(premise.BEHAVIOR);
    }

    @Override
    public int hashCode()
    {
        return BEHAVIOR.hashCode();
    }
}
