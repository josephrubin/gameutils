package box.shoe.gameutils.ai;

import java.util.ArrayDeque;

import box.shoe.gameutils.Updatable;

public class AI implements Updatable
{
    private final Clause[] CLAUSES;
    private ArrayDeque<Behavior> stack;
    private boolean justSwitched;

    public AI(Behavior startingBehavior, Clause[] clauses)
    {
        CLAUSES = clauses;

        stack = new ArrayDeque<>();
        stack.push(startingBehavior);

        // Since we just started, set the flag so that the first Behavior's enter() will be called.
        justSwitched = true;
    }

    @Override
    public void update()
    {
        Behavior currentBehavior = stack.peek();

        if (justSwitched)
        {
            currentBehavior.enter();
            justSwitched = false;
        }

        // Run the current Behavior at least once before checking the change conditions.
        currentBehavior.behave();
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
                        if (predicate.CONDITION.check())
                        {
                            // Run the Predicate's Result, and then break.
                            // Only the first Predicate whose Condition is true is activated.
                            currentBehavior.exit();
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
