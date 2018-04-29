package box.shoe.gameutils.ai;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;

import box.shoe.gameutils.Entity;
import box.shoe.gameutils.Updatable;
//TODO: should AI by default not have control() and behaviors not take an Entity and define EntityAI as we did with EnemyAI?
public class AI implements Updatable
{
    private final Clause[] CLAUSES;
    private ArrayDeque<Behavior> stack;
    private boolean justSwitched;
    private Entity controlled;

    public AI(Behavior startingBehavior, Clause[] clauses)
    {
        CLAUSES = clauses;

        stack = new ArrayDeque<>();
        stack.push(startingBehavior);

        // Since we just started, set the flag so that the first Behavior's enter() will be called.
        justSwitched = true;
    }

    public void control(@NonNull Entity entity)
    {
        controlled = entity;
    }

    @Override
    public void update()
    {
        if (controlled == null)
        {
            throw new IllegalStateException("AI does not know what to control! Please call control(Entity) before updating.");
        }

        Behavior currentBehavior = stack.peek();

        if (justSwitched)
        {
            currentBehavior.enter(controlled);
            justSwitched = false;
        }

        // Run the current Behavior at least once before checking the change conditions.
        currentBehavior.behave(controlled);
        // Every update we see if any of the Predicates should be triggered to change the current Behavior.
        // Find what Clause we are in.
        for (Clause clause : CLAUSES)
        {
            for (Premise premise : clause.PREMISES)
            {
                // We are using reference equality on purpose because only one instance of each Behavior is created.
                if (premise.BEHAVIOR == currentBehavior)
                {
                    // We have found the correct clause.
                    // Now check the Predicates in order.
                    for (Predicate predicate : clause.PREDICATES)
                    {
                        // See if the Predicate's Condition is true.
                        if (predicate.CONDITION.check(controlled))
                        {
                            // Run the Predicate's Result, and then break.
                            // Only the first Predicate whose Condition is true is activated.
                            currentBehavior.exit(controlled);
                            predicate.RESULT.resolve(stack);
                            justSwitched = true;
                            return;
                        }
                    }
                }
            }
        }
    }
}
