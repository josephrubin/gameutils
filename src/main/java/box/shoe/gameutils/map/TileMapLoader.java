package box.shoe.gameutils.map;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;

public class TileMapLoader
{//TODO: we will want to add methods for parsing from some way other than an asset file path. E.G. input stream, or string.
    private static java.util.Map<String, Tileset> cachedTilesets = new java.util.HashMap<>();

    public static TileMap fromXml(AssetManager assetManager, String xmlFilePath) throws IOException
    {
        // We will use a DOM parser. Create the factory with settings as unobtrusive as possible.
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setValidating(false);

        // Use the factory to create the builder to parse our file, then parse into a Document.
        DocumentBuilder xmlDocumentBuilder;
        try
        {
            xmlDocumentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new IllegalArgumentException("ParserConfigurationException occurred: " + e.getMessage());
        }
        Document xmlDocument;
        try
        {
            xmlDocument = xmlDocumentBuilder.parse(assetManager.open(xmlFilePath));
        }
        catch (SAXException e)
        {
            throw new IllegalArgumentException("SAXException occurred while parsing XML: " + e.getMessage());
        }

        // Get the root 'map' element.
        Element mapElement = xmlDocument.getDocumentElement();

        // Get some of the 'map' attributes for building a TileMap later.
        final int TILE_MAP_TILES_PER_ROW = Integer.parseInt(mapElement.getAttribute("width"));
        final int TILE_MAP_TILES_PER_COLUMN = Integer.parseInt(mapElement.getAttribute("height"));
        final int TILE_MAP_TILE_WIDTH_PX = Integer.parseInt(mapElement.getAttribute("tilewidth"));
        final int TILE_MAP_TILE_HEIGHT_PX = Integer.parseInt(mapElement.getAttribute("tileheight"));

        // Get all of the 'tileset' elements.
        NodeList tilesetNodeList = mapElement.getElementsByTagName("tileset");
        int tilesetNodeListLength = tilesetNodeList.getLength();

        Tileset[] tilesets = new Tileset[tilesetNodeListLength];
        for (int i = 0; i < tilesetNodeListLength; i++)
        {
            // Get the 'tileset' element. We know they're Element Nodes (getElementsByTagName), so this cast is valid.
            Element tilesetElement = (Element) tilesetNodeList.item(i);
            // There is only one image for each tileset. We can't use firstChild because there are text/attribute nodes.
            Element tilesetImageElement = (Element) tilesetElement.getElementsByTagName("image").item(0);
            Tileset tileset;

            // Save the source of the tileset's image now, since we will only get the
            // other attributes if we do not find this tileset in the cache.
            String tilesetImageElementSource = tilesetImageElement.getAttribute("source");

            if (cachedTilesets.containsKey(tilesetImageElementSource))
            {
                // We found the tileset in the cache, so simply retrieve it.
                tileset = cachedTilesets.get(tilesetImageElementSource);
            }
            else
            {
                // The tileset was not in the cache, so we must create a new one.
                // First, load up the image from its source, and create a Bitmap... //todo: use options to set inImmutable = true
                Bitmap image = BitmapFactory.decodeStream(assetManager.open(tilesetImageElementSource.substring(3))); //fixme: somehow fix uri's which are relative to tmx file, they should be relative to assets folder.

                // ...then create the new tileset...
                tileset = new Tileset(
                        tilesetElement.getAttribute("name"),
                        Integer.parseInt(tilesetElement.getAttribute("firstgid")),
                        Integer.parseInt(tilesetElement.getAttribute("tilewidth")),
                        Integer.parseInt(tilesetElement.getAttribute("tileheight")),
                        tilesetImageElementSource,
                        Integer.parseInt(tilesetImageElement.getAttribute("width")),
                        Integer.parseInt(tilesetImageElement.getAttribute("height")),
                        image);

                // ...and add it to the cache.
                cachedTilesets.put(tilesetImageElementSource, tileset);
            }

            tilesets[i] = tileset;
        }

        // Now we will process each layer one by one.
        NodeList layerNodeList = mapElement.getElementsByTagName("layer"); //todo: if invisible layer, do not generate bitmap
        int layerNodeListLength = layerNodeList.getLength();

        Layer[] layers = new Layer[layerNodeListLength];

        for (int i = 0; i < layerNodeListLength; i++)
        {
            Element layerElement = (Element) layerNodeList.item(i);

            byte[][] layerRelativeTileGidGrid = new byte[TILE_MAP_TILES_PER_ROW][TILE_MAP_TILES_PER_COLUMN];
            byte[][] layerAbsoluteTileGidGrid = new byte[TILE_MAP_TILES_PER_ROW][TILE_MAP_TILES_PER_COLUMN];

            // And for each layer, process the tiles one by one.
            NodeList tileNodeList = layerElement.getElementsByTagName("tile");
            int tileNodeListLength = tileNodeList.getLength();
            for (int j = 0; j < tileNodeListLength; j++)
            {
                Element tileElement = (Element) tileNodeList.item(j);
                int absoluteTileGid = Integer.parseInt(tileElement.getAttribute("gid"));
                int relativeTileGid = -1;

                if (absoluteTileGid > Byte.MAX_VALUE) //TODO: this is a silly limitation for the absolute tile gids
                {
                    throw new IllegalArgumentException("Found an absolute tile GID more than " + Byte.MAX_VALUE + "!" +
                            "Too many different tiles!");
                }

                // We want to get the relative GID for our tile, so search the tilesets backwards.
                Tileset tileTileset = null;
                for (int k = tilesets.length - 1; k >= 0; k--)
                {
                    Tileset tileset = tilesets[k];
                    if (tileset.FIRST_GID <= absoluteTileGid)
                    {
                        tileTileset = tileset;
                        relativeTileGid = absoluteTileGid - tileset.FIRST_GID; //todo: we should add one to be consistent with GIDs starting at 1, but i think that the current way (start at 0) is better
                        if (relativeTileGid > Byte.MAX_VALUE)
                        {
                            throw new IllegalArgumentException("Tileset (" + tileset.NAME + ") has more than " + Byte.MAX_VALUE + " tiles!");
                        }
                        break;
                    }
                }
                if (tileTileset == null)
                {
                    throw new IllegalArgumentException("Malformed XML! Found a tile (" + tileElement + "+ whose GID" +
                            " (" + absoluteTileGid + ") is not contained in any tileset!");
                }
                int tileColumn = j % TILE_MAP_TILES_PER_ROW;
                int tileRow = j / TILE_MAP_TILES_PER_ROW;
                layerRelativeTileGidGrid[tileColumn][tileRow] = (byte) relativeTileGid;
                layerAbsoluteTileGidGrid[tileColumn][tileRow] = (byte) absoluteTileGid;

                Layer layer = new Layer(
                        layerElement.getAttribute("name"),
                        layerRelativeTileGidGrid,
                        layerAbsoluteTileGidGrid);
                layers[i] = layer;
            }
        }

        return new TileMap(
                TILE_MAP_TILES_PER_ROW,
                TILE_MAP_TILES_PER_COLUMN,
                TILE_MAP_TILE_WIDTH_PX,
                TILE_MAP_TILE_HEIGHT_PX,
                tilesets,
                layers);
    }
}
