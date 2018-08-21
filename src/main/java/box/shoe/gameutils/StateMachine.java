package box.shoe.gameutils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import box.shoe.gameutils.CollectionUtils;

/**
 * An item whose internal state can be exactly one of a predefined collection,
 * and whose current state can be queried at any time.
 */

public class StateMachine
{
    private Set<String> states;
    private String currentState;

    public StateMachine(String currentState, String... possibleStates)
    {
        states = new HashSet<>(Arrays.asList(possibleStates));
        enterState(currentState);
    }

    public void enterState(String state)
    {
        if (states.contains(state))
        {
            currentState = state;
        }
        else
        {
            throw new IllegalArgumentException(state + " is not a valid state!");
        }
    }

    public boolean inState(String... states)
    {
        for (String state : states)
        {
            if (state.equals(currentState))
            {
                return true;
            }
        }

        return false;
    }

    public String getCurrentState()
    {
        return currentState;
    }
}
