package box.shoe.gameutils.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

public class TileMap
{
    // Number of tiles in each row of this map. Also the number of columns.
    public final int TILES_PER_ROW;
    // Number of tiles in each column of this map. Also the number of rows.
    public final int TILES_PER_COLUMN;

    // Width of each tile in pixels.
    public final int TILE_WIDTH_PX;
    // Height of each tile in pixels.
    public final int TILE_HEIGHT_PX;

    // Total width in pixels of this map. Equivalent to TILES_PER_ROW * TILE_WIDTH_PX.
    public final int TOTAL_WIDTH_PX;
    // Total height in pixels of this map. Equivalent to TILES_PER_COLUMN * TILE_HEIGHT_PX.
    public final int TOTAL_HEIGHT_PX;

    // Only the Tilesets that tiles from any Layer in this TileMap come from.
    private final Tileset[] TILESETS;
    // All of the Layers in this TileMap, accessed by their name.
    private final Map<String, Layer> LAYER_MAP;

    /**
     * Create a TileMap.
     * @param tilesPerRow number of tiles in each row of this map. Also the number of columns.
     * @param tilesPerColumn number of tiles in each column of this map. Also the number of rows.
     * @param tileWidthPx width of each tile in pixels.
     * @param tileHeightPx height of each tile in pixels.
     */
    /* pack */ TileMap(int tilesPerRow, int tilesPerColumn, int tileWidthPx, int tileHeightPx, Tileset[] tilesets, Layer[] layers)
    {
        TILES_PER_ROW = tilesPerRow;
        TILES_PER_COLUMN = tilesPerColumn;

        TILE_WIDTH_PX = tileWidthPx;
        TILE_HEIGHT_PX = tileHeightPx;

        TOTAL_WIDTH_PX = tilesPerRow * tileWidthPx;
        TOTAL_HEIGHT_PX = tilesPerColumn * tileHeightPx;

        TILESETS = tilesets;

        LAYER_MAP = new HashMap<>();
        for (int i = 0; i < layers.length; i++)
        {
            Layer layer = layers[i];
            LAYER_MAP.put(layer.NAME, layer);
        }
    }

    public Bitmap generateLayerBitmap(String layerName)
    {
        Layer layer = getLayer(layerName);
        byte[][] layerRelativeTileGidGrid = layer.RELATIVE_TILE_GID_GRID;
        byte[][] layerAbsoluteTileGidGrid = layer.ABSOLUTE_TILE_GID_GRID;

        // Define rectangles for copying pixels when building the Bitmap for this Layer.
        Rect transferPixelsSource = new Rect();
        Rect transferPixelsDest = new Rect();

        Bitmap layerBitmap = Bitmap.createBitmap(
                TILES_PER_ROW * TILE_WIDTH_PX,
                TILES_PER_COLUMN * TILE_HEIGHT_PX,
                Bitmap.Config.ARGB_8888);
        Canvas layerBitmapCanvas = new Canvas(layerBitmap);

        // Process the tiles one by one.
        for (int i = 0; i < TILES_PER_ROW; i++)
        {
            for (int j = 0; j < TILES_PER_COLUMN; j++)
            {
                int relativeTileGid = layerRelativeTileGidGrid[i][j];
                int absoluteTileGid = layerAbsoluteTileGidGrid[i][j];

                if (absoluteTileGid == TileMapLoader.EMPTY_TILE)
                {
                    // Skip empty tiles.
                    continue;
                }

                // We want to get the Tileset our tile, so search the tilesets backwards.
                Tileset tileTileset = null;
                for (int k = TILESETS.length - 1; k >= 0; k--)
                {
                    Tileset tileset = TILESETS[k];
                    if (tileset.FIRST_GID <= absoluteTileGid)
                    {
                        tileTileset = tileset;
                        break;
                    }
                }

                int destX = i * TILE_WIDTH_PX;
                int destY = j * TILE_HEIGHT_PX;

                int sourceX = (relativeTileGid % (tileTileset.IMAGE_WIDTH_PX / tileTileset.TILE_WIDTH_PX)) * TILE_WIDTH_PX;
                int sourceY = (relativeTileGid / (tileTileset.IMAGE_WIDTH_PX / tileTileset.TILE_WIDTH_PX)) * TILE_HEIGHT_PX;

                // Assemble the layer bitmap with each tile.
                transferPixelsSource.set(sourceX, sourceY, sourceX + TILE_WIDTH_PX, sourceY + TILE_HEIGHT_PX);
                transferPixelsDest.set(destX, destY, destX + TILE_WIDTH_PX, destY + TILE_HEIGHT_PX);
                layerBitmapCanvas.drawBitmap(tileTileset.IMAGE, transferPixelsSource, transferPixelsDest, null);
            }
        }

        return layerBitmap;
    }

    public byte[][] generateLayerRelativeTileGidGrid(String layerName)
    {
        return getLayer(layerName).RELATIVE_TILE_GID_GRID.clone();
    }

    public byte[][] generateLayerAbsoluteTileGidGrid(String layerName)
    {
        return getLayer(layerName).ABSOLUTE_TILE_GID_GRID.clone();
    }

    private Layer getLayer(String layerName)
    {
        if (!LAYER_MAP.containsKey(layerName))
        {
            throw new IllegalArgumentException("TileMap does not contain a layer '" + layerName + "'!");
        }
        return LAYER_MAP.get(layerName);
    }

    @Override
    public String toString()
    {
        return "TileMap{" +
                "TILES_PER_ROW=" + TILES_PER_ROW +
                ", TILES_PER_COLUMN=" + TILES_PER_COLUMN +
                ", TILE_WIDTH_PX=" + TILE_WIDTH_PX +
                ", TILE_HEIGHT_PX=" + TILE_HEIGHT_PX +
                ", TOTAL_WIDTH_PX=" + TOTAL_WIDTH_PX +
                ", TOTAL_HEIGHT_PX=" + TOTAL_HEIGHT_PX +
                '}';
    }
}