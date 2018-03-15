package box.shoe.gameutils.engine;

import android.view.MotionEvent;

import box.shoe.gameutils.Renderable;
import box.shoe.gameutils.Updatable;

/**
 * Created by Joseph on 3/6/2018.
 *
 */

public interface Game extends Updatable, Renderable
{
    // _______________
    // GAME LIFECYCLE.

    /**
     * Called once, right before updates and renders begin.
     * This will never be called more than one time.
     * Used for initialization that is based on screen dimensions.
     * @param screenWidth the width of this Game's Screen.
     * @param screenHeight the height of this Game's Screen.
     */
    void onStart(int screenWidth, int screenHeight);

    /**
     * Called once, right after updates and renders stop.
     * This will never be called more than one time.
     * Used to clean up resources.
     */
    void onStop();

    /** //fixme: this is never called.
     * Called whenever the size of this Game's Screen changes since onStart was called.
     * @param newScreenWidth the new width of this Game's Screen.
     * @param newScreenHeight the new height of this Game's Screen.
     * @param oldScreenWidth the old width of this Game's Screen.
     * @param oldScreenHeight the old height of this Game's Screen.
     */
    void onScreenSizeChanged(int newScreenWidth, int newScreenHeight, int oldScreenWidth, int oldScreenHeight);

    // ______
    // INPUT.

    /**
     * Called when the player touches the touchscreen to interact with the Game.
     * May not be called precisely when the touch event happens, because onTouch is synchronized with update and render,
     * so do not rely on exact timings, but an event that occurs before another event will always be passed into
     * onTouch first.
     * @param touchEvent the touchscreen event that occurred.
     */
    void onTouch(MotionEvent touchEvent);

    // ______________
    // CONFIGURATION.

    /**
     * Called by the Engine to get the UPS that that Game would like to operate at.
     * @return the target UPS, which should be a factor of 1000 for best looking results.
     */
    @Engine.UPS_Options int getTargetUpdatesPerSecond(); //TODO: if we have many config options, consider a config object
}