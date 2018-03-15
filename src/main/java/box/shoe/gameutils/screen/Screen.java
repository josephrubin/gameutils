package box.shoe.gameutils.screen;

import android.graphics.Canvas;
import android.support.annotation.CheckResult;
import android.view.View;

/**
 * Created by Joseph on 1/1/2018.
 * An object which can display a game to the Android user.
 */

public interface Screen
{
    // __________
    // RENDERING.

    /**
     * Begin the process of rendering a frame to this Screen, which will be drawn onto a View.
     * Do not call if isActive() returns false.
     * Do not call if isRendering() returns true.
     * @return the Canvas that should be drawn to for pixels to appear on the View. This Canvas may only be touched
     *          by the thread that called startRender().
     */
    @CheckResult
    Canvas startRender();

    /**
     * Checks if the Screen is in the process of rendering a frame.
     * @return true if isRender() was called more recently than endRender(), and false otherwise.
     */
    boolean isRendering();

    /**
     * End the process of rendering a frame, and show the pixels draw to the Canvas returned by startRender() to the
     * underlying View.
     */
    void endRender();

    // ________________
    // EVENT LISTENERS.

    // should run right away if event already happened. auto removed when surface destroyed (view removed from screen (onStop called in Activity))
    void setReadyListener(ReadyListener readyListener);
    void removeReadyListener();

    // auto removed when surface destroyed (view removed from screen (onStop called in Activity))
    void setSizeChangedListener(SizeChangedListener sizeChangedListener);
    void removeSizeChangedListener();

    boolean isActive();

    ///// OLD V V V V
    //       todo: figure out whats needed and what is not
    void cleanup();

    int getWidth();
    int getHeight();

    View asView();
    // ^^^^^^^^^^^^^

    interface ReadyListener
    {
        void onReady(int screenWidth, int screenHeight);
    }

    interface SizeChangedListener
    {
        void onSizeChanged(int newScreenWidth, int newScreenHeight, int oldScreenWidth, int oldScreenHeight);
    }
}
