package box.shoe.gameutils;

/**
 * Created by Joseph on 3/14/2018.
 */
//todo: this class should not exist, move methods elsewhere.
public class Interpolation
{
    public static float interpolateFloat(float past, float current, double interpolationRatio)
    {
        return (float) (past * (1 - interpolationRatio) + current * interpolationRatio);
    }
}
