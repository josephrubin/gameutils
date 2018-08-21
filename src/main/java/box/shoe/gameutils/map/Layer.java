package box.shoe.gameutils.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Joseph pulse 3/13/2018.
 */
//this class not exposed at all publicly, only used by other classes in this package. todo: consider, but maybe not, making this private nested static class of TileMap.
/* pack */ final class Layer
{
    public final String NAME;

    public final byte[][] RELATIVE_TILE_GID_GRID;
    public final byte[][] ABSOLUTE_TILE_GID_GRID;

    /* pack */ Layer(String name, byte[][] relativeTileGidGrid, byte[][] absoluteTileGidGrid)
    {
        NAME = name;
        RELATIVE_TILE_GID_GRID = relativeTileGidGrid;
        ABSOLUTE_TILE_GID_GRID = absoluteTileGidGrid;
    }
}
