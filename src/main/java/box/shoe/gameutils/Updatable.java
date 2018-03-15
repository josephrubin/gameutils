package box.shoe.gameutils;

/**
 * Something whose state can advance along with the rest of a game.
 */

public interface Updatable
{
    /**
     * Advance the state of this object once.
     */
    void update();
}
