package box.shoe.gameutils.debug;

import android.os.SystemClock;
import android.util.Log;

/**
 * Logs the amount of time taken by a section of code.
 */

public class Benchmarker
{
    private static long currentThreadTimeMS;
    private static long currentOverallTimeMS;

    /**
     * Begin benchmarking code.
     */
    public static void start()
    {
        currentThreadTimeMS = SystemClock.currentThreadTimeMillis();
        currentOverallTimeMS = SystemClock.uptimeMillis();
    }

    /**
     * End benchmarking and Log.d the amount of thread time in milliseconds taken since start() was last called.
     * Uses SystemClock.currentThreadTimeMillis() time base.
     */
    public static void resultThread()
    {
        Log.d("BENCHMARKER", String.valueOf(SystemClock.currentThreadTimeMillis() - currentThreadTimeMS));
    }

    /**
     * End benchmarking and Log.d the amount of time in milliseconds taken since start() was last called.
     * Uses SystemClock.uptimeMillis() time base.
     */
    public static void resultOverall()
    {
        Log.d("BENCHMARKER", String.valueOf(SystemClock.uptimeMillis() - currentOverallTimeMS));
    }
}
