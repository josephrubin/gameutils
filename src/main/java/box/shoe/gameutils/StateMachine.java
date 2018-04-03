package box.shoe.gameutils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import box.shoe.gameutils.CollectionUtils;

/**
 * Created by Joseph on 3/21/2018.
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
