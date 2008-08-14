package de.jacavi.appl.track;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.graphics.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.jacavi.rcp.Activator;



public class TilesetRepository {

    public enum TileSet {
        DEBUG, ANALOGUE, DIGITAL;
    }

    @SuppressWarnings("serial")
    public class TilesetRepositoryInitializationFailedException extends Exception {
        public TilesetRepositoryInitializationFailedException(Exception e) {
            super(e);
        }

        public TilesetRepositoryInitializationFailedException(String message) {
            super(message);
        }
    }

    private Map<TileSet, SortedMap<String, Tile>> tiles = new HashMap<TileSet, SortedMap<String, Tile>>();

    public TilesetRepository(String configurationFile) throws TilesetRepositoryInitializationFailedException {
        try {
            // parse the XML file
            Document document = parseConfigurationFile(configurationFile);

            // prepare the tileset maps
            for(TileSet tileset: TileSet.values())
                tiles.put(tileset, new TreeMap<String, Tile>());

            // iterate over all tilesets
            NodeList tilesets = document.getElementsByTagName("tileset");
            for(int i = 0; i < tilesets.getLength(); i++)
                importTileset((Element) tilesets.item(i));
        } catch(Exception e) {
            throw new TilesetRepositoryInitializationFailedException(e);
        }
    }

    private Document parseConfigurationFile(String filename) throws ParserConfigurationException, SAXException,
            IOException {
        InputStream is = Activator.getResourceAsStream(filename);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = null;
        domBuilder = domFactory.newDocumentBuilder();
        return domBuilder.parse(is);
    }

    private void importTileset(Element tilesetElement) throws TilesetRepositoryInitializationFailedException,
            IOException {
        // try to find the enum entry for this tileset
        String tilesetID = tilesetElement.getAttribute("id");
        TileSet tilesetType = TileSet.valueOf(tilesetID.toUpperCase());
        if(tilesetType == null)
            throw new TilesetRepositoryInitializationFailedException("Unknown tileset ID: " + tilesetID);

        // iterate over all tiles in this tileset
        NodeList tileNodes = tilesetElement.getElementsByTagName("tile");
        for(int i = 0; i < tileNodes.getLength(); i++)
            importTile(tilesetType, (Element) tileNodes.item(i));

        // make sure exactly one of the tiles is marked as the initial tile
        boolean initialTileFound = false;
        for(Tile t: tiles.get(tilesetType).values())
            if(t.isInitial())
                if(initialTileFound)
                    throw new TilesetRepositoryInitializationFailedException("The tileset " + tilesetID
                            + " contains at least two different tiles marked as initial.");
                else
                    initialTileFound = true;
        if(!initialTileFound)
            throw new TilesetRepositoryInitializationFailedException("The tileset " + tilesetID
                    + " is missing a tile marked as initial");
    }

    private void importTile(TileSet tileset, Element tileElement) throws IOException {
        // read the data for this tile from the XML
        String tileID = tileElement.getAttribute("id");
        String tileName = tileElement.getAttribute("name");
        boolean isInitial = tileElement.hasAttribute("isInitial")
                && tileElement.getAttribute("isInitial").equals("true");
        String filename = null;
        Point entryPoint = null;
        Point exitPoint = null;
        int entryToExitAngle = 0;

        NodeList tileDataNodes = tileElement.getChildNodes();
        for(int i = 0; i < tileDataNodes.getLength(); i++) {
            if(tileDataNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element tileDataElement = (Element) tileDataNodes.item(i);
                if(tileDataElement.getNodeName().equals("filename")) {
                    filename = "/tiles/" + tileset.toString().toLowerCase() + "/" + tileDataElement.getTextContent();
                } else if(tileDataElement.getNodeName().equals("entryPoint")) {
                    entryPoint = new Point(Integer.valueOf(tileDataElement.getAttribute("x")), Integer
                            .valueOf(tileDataElement.getAttribute("y")));
                } else if(tileDataElement.getNodeName().equals("exitPoint")) {
                    exitPoint = new Point(Integer.valueOf(tileDataElement.getAttribute("x")), Integer
                            .valueOf(tileDataElement.getAttribute("y")));
                } else {
                    entryToExitAngle = Integer.valueOf(Integer.valueOf(tileDataElement.getTextContent()));
                }
            }
        }

        tiles.get(tileset)
                .put(tileID, new Tile(filename, tileName, isInitial, entryPoint, exitPoint, entryToExitAngle));
    }

    public Tile getTile(TileSet tileSet, String id) {
        return tiles.get(tileSet).get(id);
    }

    /**
     * Returns a list of all available tiles of a specific tileset.
     * <p>
     * Note that the list returned by this method returns some tiles multiple times in case they can be used from
     * different directions.
     */
    public Map<String, Tile> getAvailableTiles(TileSet tileset) {
        // TODO: implement returning inverted tiles
        SortedMap<String, Tile> result = new TreeMap<String, Tile>(tiles.get(tileset));

        // remove the initial tile
        for(String tileID: result.keySet())
            if(result.get(tileID).isInitial()) {
                result.remove(tileID);
                break;
            }

        return result;
    }

    public Tile getInitialTile(TileSet tileset) {
        for(Tile t: tiles.get(tileset).values())
            if(t.isInitial())
                return t;
        throw new RuntimeException(
                "Unexpected point in code reached: no initial tile was found in a tileset in TilesetRepository.getInitialTile()");
    }

}
