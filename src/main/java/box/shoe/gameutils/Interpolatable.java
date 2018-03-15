package box.shoe.gameutils;

/**
 * Created by Joseph on 3/15/2018.
 */

public interface Interpolatable
{
    int getInterpValuesArrayMaxIndex();

    void saveInterpValues(float[] out);
    void loadInterpValues(float[] in);

    Service<Interpolatable> INTERPOLATABLE_SERVICE = new Service<>();
}
