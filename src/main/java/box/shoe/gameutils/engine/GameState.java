package box.shoe.gameutils.engine;

import java.util.HashMap;
import java.util.Map;

import box.shoe.gameutils.Interpolatable;

/**
 * The job of a GameState is two fold.
 * 1) Keep track of a time stamp which is used for generated an interpolation ratio for rendering.
 * 2) Keep track of all Interpolatables that exist at the update that generated this GameState
 *      so that they can be interpolated.
 * Crucially, it is NOT a save state. A GameState is a representation of a point in time, but does not hold all
 * game data at that point.
 */

/* pack */ class GameState
{
    // The time at which the update which generated this GameState occurred.
    private long timeStamp;

    // Maps each Interpolatable to the values that it wishes to interpolate.
    private Map<Interpolatable, float[]> savedInterpValues;

    /**
     * Create a GameState.
     */
    public GameState()
    {
        savedInterpValues = new HashMap<>();
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public Map<Interpolatable, float[]> getSavedInterpValues()
    {
        return savedInterpValues;
    }

    /*
    private static class Factory implements FactoryObjectPool.Factory<GameState>
    {
        @Override
        public GameState create()
        {
            return new GameState();
        }
    }
    */
}
