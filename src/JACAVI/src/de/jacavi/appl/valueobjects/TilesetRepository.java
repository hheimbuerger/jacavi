package de.jacavi.appl.valueobjects;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    public class TilesetRepositoryInitializationFailedException extends Exception {
        public TilesetRepositoryInitializationFailedException(Exception e) {
            super(e);
        }

        public TilesetRepositoryInitializationFailedException(String message) {
            super(message);
        }
    }

    private Map<TileSet, Map<String, Tile>> tiles = new HashMap<TileSet, Map<String, Tile>>();

    public TilesetRepository(String configurationFile) throws TilesetRepositoryInitializationFailedException {
        try {
            // parse the XML file
            Document document = parseConfigurationFile(configurationFile);

            // prepare the tileset maps
            for(TileSet tileset: TileSet.values())
                tiles.put(tileset, new HashMap<String, Tile>());

            // iterate over all tilesets
            NodeList tilesets = document.getElementsByTagName("tileset");
            for(int i = 0; i < tilesets.getLength(); i++)
                importTileset((Element) tilesets.item(i));
        } catch(Exception e) {
            throw new TilesetRepositoryInitializationFailedException(e);
        }

        // HACK: just for debugging purposes, should actually be read from configuration file
        /*Map<String, Tile> digitalTiles = new HashMap<String, Tile>();
        digitalTiles.put("finishingStraight", new Tile("finishing_straight.png", new Point(-53, +6),
                new Point(+53, +6), new Angle(0)));
        digitalTiles.put("straight", new Tile("straight.png", new Point(-26, 0), new Point(+26, 0), new Angle(0)));
        digitalTiles.put("turn90deg", new Tile("turn_90deg.png", new Point(-12, 0), new Point(0, -12), new Angle(-90)));
        digitalTiles.put("turn30deg", new Tile("turn_30deg.png", new Point(-8, 0), new Point(-2, -2), new Angle(-30)));
        tiles.put(TileSet.digital, digitalTiles);*/
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
    }

    private void importTile(TileSet tileset, Element tileElement) throws IOException {
        // read the data for this tile from the XML
        String tileID = tileElement.getAttribute("id");
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

        tiles.get(tileset).put(tileID, new Tile(filename, entryPoint, exitPoint, entryToExitAngle));
    }

    public Tile getTile(TileSet tileSet, String id) {
        return tiles.get(tileSet).get(id);
    }

}
