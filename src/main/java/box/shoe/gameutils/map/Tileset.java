package box.shoe.gameutils.map;

import android.graphics.Bitmap;

/**
 * Created by Joseph on 3/13/2018.
 */

/* pack */ final class Tileset
{//todo: needs a cleanup method to recycle the bitmap
    public final String NAME;

    public final int FIRST_GID;

    public final int TILE_WIDTH_PX;
    public final int TILE_HEIGHT_PX;

    public final String IMAGE_PATH;
    public final int IMAGE_WIDTH_PX;
    public final int IMAGE_HEIGHT_PX;
    public final Bitmap IMAGE;

    /* pack */ Tileset(String name, int firstGid, int tileWidth, int tileHeight, String imagePath, int imageWidth, int imageHeight, Bitmap image)
    {
        NAME = name;
        FIRST_GID = firstGid;
        TILE_WIDTH_PX = tileWidth;
        TILE_HEIGHT_PX = tileHeight;
        IMAGE_PATH = imagePath;
        IMAGE_WIDTH_PX = imageWidth;
        IMAGE_HEIGHT_PX = imageHeight;
        IMAGE = image;
    }

    @Override
    public String toString()
    {
        return "Tileset{" +
                "NAME='" + NAME + '\'' +
                ", FIRST_GID=" + FIRST_GID +
                ", TILE_WIDTH_PX=" + TILE_WIDTH_PX +
                ", TILE_HEIGHT_PX=" + TILE_HEIGHT_PX +
                ", IMAGE_PATH='" + IMAGE_PATH + '\'' +
                ", IMAGE_WIDTH_PX=" + IMAGE_WIDTH_PX +
                ", IMAGE_HEIGHT_PX=" + IMAGE_HEIGHT_PX +
                ", IMAGE=" + IMAGE +
                '}';
    }
}