package de.jacavi.appl.track;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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



public class TilesetRepository {

    @SuppressWarnings("serial")
    public static class TilesetRepositoryInitializationFailedException extends Exception {
        public TilesetRepositoryInitializationFailedException(Exception e) {
            super(e);
        }

        public TilesetRepositoryInitializationFailedException(String message) {
            super(message);
        }
    }

    private final Map<String, Tileset> tilesets = new HashMap<String, Tileset>();

    private final File bitmapsBasePath;

    public TilesetRepository(String configurationFile, String bitmapsBasePath)
            throws TilesetRepositoryInitializationFailedException {
        try {
            // make sure the base path for the bitmaps exists
            this.bitmapsBasePath = new File(bitmapsBasePath);
            if(!this.bitmapsBasePath.exists())
                throw new TilesetRepositoryInitializationFailedException(
                        "The specified base path for the tile bitmaps does not exist: "
                                + this.bitmapsBasePath.getCanonicalPath());

            // parse the XML file
            Document document = parseConfigurationFile(configurationFile);

            // iterate over all tilesets
            NodeList tilesets = document.getElementsByTagName("tileset");
            for(int i = 0; i < tilesets.getLength(); i++)
                importTileset((Element) tilesets.item(i));
        } catch(TilesetRepositoryInitializationFailedException e) {
            throw e;
        } catch(Exception e) {
            throw new TilesetRepositoryInitializationFailedException(e);
        }
    }

    private Document parseConfigurationFile(String filename) throws ParserConfigurationException, SAXException,
            IOException {
        InputStream is = new FileInputStream(filename);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = null;
        domBuilder = domFactory.newDocumentBuilder();
        return domBuilder.parse(is);
    }

    private void importTileset(Element tilesetElement) throws TilesetRepositoryInitializationFailedException,
            IOException {
        // create a new tileset instance
        String tilesetID = tilesetElement.getAttribute("id");
        int laneCount = Integer.valueOf(tilesetElement.getAttribute("laneCount"));
        Tileset tileset = new Tileset(tilesetID, laneCount);

        // iterate over all tiles in this tileset
        NodeList tileNodes = tilesetElement.getElementsByTagName("tile");
        for(int i = 0; i < tileNodes.getLength(); i++)
            importTile(tileset, (Element) tileNodes.item(i));

        tileset.validate();
        tilesets.put(tilesetID, tileset);
    }

    private void importTile(Tileset tileset, Element tileElement) throws IOException,
            TilesetRepositoryInitializationFailedException {
        // read the data for this tile from the XML
        String tileID = tileElement.getAttribute("id");
        String tileName = tileElement.getAttribute("name");
        boolean isInitial = tileElement.hasAttribute("isInitial")
                && tileElement.getAttribute("isInitial").equals("true");
        File file = null;
        Point entryPoint = null;
        Point exitPoint = null;
        int entryToExitAngle = 0;
        List<Lane> lanes = new ArrayList<Lane>();
        int laneIndex = 0;
        List<StartingPoint> startingPoints = new ArrayList<StartingPoint>();

        NodeList tileDataNodes = tileElement.getChildNodes();
        for(int i = 0; i < tileDataNodes.getLength(); i++) {
            if(tileDataNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element tileDataElement = (Element) tileDataNodes.item(i);
                if(tileDataElement.getNodeName().equals("filename")) {
                    file = new File(new File(bitmapsBasePath, tileset.getId()), tileDataElement.getTextContent());
                } else if(tileDataElement.getNodeName().equals("entryPoint")) {
                    entryPoint = new Point(Integer.valueOf(tileDataElement.getAttribute("x")), Integer
                            .valueOf(tileDataElement.getAttribute("y")));
                } else if(tileDataElement.getNodeName().equals("exitPoint")) {
                    exitPoint = new Point(Integer.valueOf(tileDataElement.getAttribute("x")), Integer
                            .valueOf(tileDataElement.getAttribute("y")));
                } else if(tileDataElement.getNodeName().equals("entryToExitAngle")) {
                    entryToExitAngle = Integer.valueOf(Integer.valueOf(tileDataElement.getTextContent()));
                } else if(tileDataElement.getNodeName().equals("lane")) {
                    lanes.add(importLane(tileset, tileDataElement, laneIndex));
                    laneIndex++;
                } else if(tileDataElement.getNodeName().equals("startingPoint")) {
                    int startingLaneIndex = Integer.valueOf(tileDataElement.getAttribute("lane"));
                    if(startingLaneIndex >= lanes.size())
                        throw new TilesetRepositoryInitializationFailedException("Tile " + tileID + " of tileset "
                                + tileset.getId() + " refers to lane index " + startingLaneIndex + " but only "
                                + lanes.size() + " lanes are defined at this point.");
                    startingPoints.add(new StartingPoint(startingLaneIndex, Integer.valueOf(tileDataElement
                            .getAttribute("steps"))));
                } else {
                    throw new TilesetRepositoryInitializationFailedException("Invalid element in tile: "
                            + tileDataElement.getNodeName());
                }
            }
        }

        tileset.add(tileID, new Tile(tileID, file, tileName, isInitial, entryPoint, exitPoint, entryToExitAngle, lanes
                .toArray(new Lane[lanes.size()]), startingPoints.toArray(new StartingPoint[startingPoints.size()])));
    }

    private Lane importLane(Tileset tileset, Element tileDataElement, int laneIndex)
            throws TilesetRepositoryInitializationFailedException {
        Lane lane = new Lane();
        boolean isChangeLane = tileDataElement.hasAttribute("isChangeLane")
                && tileDataElement.getAttribute("isChangeLane").equals("true");
        int regularExit = tileDataElement.hasAttribute("exitLane") ? Integer.valueOf(tileDataElement
                .getAttribute("exitLane")) : laneIndex;
        int changeExit = laneIndex;

        NodeList laneSectionNodes = tileDataElement.getChildNodes();

        if(isChangeLane) {
            for(int i = 0; i < laneSectionNodes.getLength(); i++) {
                if(laneSectionNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element laneSection = (Element) laneSectionNodes.item(i);
                    if(laneSection.getNodeName().equals("common")) {
                        importLaneSections(lane, lane.getLaneSectionsCommon(), laneSection.getChildNodes());
                    } else if(laneSection.getNodeName().equals("regular")) {
                        if(laneSection.hasAttribute("exitLane"))
                            regularExit = Integer.valueOf(laneSection.getAttribute("exitLane"));
                        importLaneSections(lane, lane.getLaneSectionsRegular(), laneSection.getChildNodes());
                    } else if(laneSection.getNodeName().equals("change")) {
                        if(laneSection.hasAttribute("exitLane"))
                            changeExit = Integer.valueOf(laneSection.getAttribute("exitLane"));
                        importLaneSections(lane, lane.getLaneSectionsChange(), laneSection.getChildNodes());
                    } else if(laneSection.getNodeName().equals("checkpoint")) {
                        lane.addCheckpoint(laneSection.getAttribute("id"), Integer.valueOf(laneSection
                                .getAttribute("steps")), Integer.valueOf(laneSection.getAttribute("x")), Integer
                                .valueOf(laneSection.getAttribute("y")));
                    } else {
                        throw new TilesetRepositoryInitializationFailedException("Invalid lane section in lane: "
                                + laneSection.getNodeName());
                    }
                }
            }
        } else
            importLaneSections(lane, lane.getLaneSectionsCommon(), laneSectionNodes);

        lane.setExits(regularExit, changeExit);

        return lane;
    }

    private void importLaneSections(Lane lane, LaneSectionList laneSectionsCommon, NodeList laneSectionNodes)
            throws TilesetRepositoryInitializationFailedException {
        for(int i = 0; i < laneSectionNodes.getLength(); i++) {
            if(laneSectionNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element laneSection = (Element) laneSectionNodes.item(i);
                if(laneSection.getNodeName().equals("line")) {
                    laneSectionsCommon.addLine(Integer.valueOf(laneSection.getAttribute("length")), Integer
                            .valueOf(laneSection.getAttribute("x1")), Integer.valueOf(laneSection.getAttribute("y1")),
                            Integer.valueOf(laneSection.getAttribute("x2")), Integer.valueOf(laneSection
                                    .getAttribute("y2")));
                } else if(laneSection.getNodeName().equals("quad-bezier")) {
                    laneSectionsCommon.addQuadBezier(Integer.valueOf(laneSection.getAttribute("length")), Integer
                            .valueOf(laneSection.getAttribute("x1")), Integer.valueOf(laneSection.getAttribute("y1")),
                            Integer.valueOf(laneSection.getAttribute("x2")), Integer.valueOf(laneSection
                                    .getAttribute("y2")), Integer.valueOf(laneSection.getAttribute("x3")), Integer
                                    .valueOf(laneSection.getAttribute("y3")), Integer.valueOf(laneSection
                                    .getAttribute("entryToExitAngle")));
                } else if(laneSection.getNodeName().equals("checkpoint")) {
                    lane.addCheckpoint(laneSection.getAttribute("id"), Integer.valueOf(laneSection
                            .getAttribute("steps")), Integer.valueOf(laneSection.getAttribute("x")), Integer
                            .valueOf(laneSection.getAttribute("y")));
                } else {
                    throw new TilesetRepositoryInitializationFailedException("Invalid lane section in lane: "
                            + laneSection.getNodeName());

                }
            }
        }
    }

    public Tileset getTileset(String id) {
        return tilesets.get(id);
    }
}
