package de.jacavi.appl.valueobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.valueobjects.TilesetRepository.TileSet;



public class Track {

    public class TrackLoadingException extends Exception {
        public TrackLoadingException(Exception e) {
            super(e);
        }
    }

    private List<TrackSection> sections = new LinkedList<TrackSection>();

    private String trackName;

    public Track(TileSet tileset) {
        TilesetRepository tilesetRepository = (TilesetRepository) ContextLoader.getBean("tilesetRepository");
        sections.add(new TrackSection(tilesetRepository.getTile(tileset, "finishingStraight")));
    }

    public Track(InputStream inputStream) throws TrackLoadingException {
        try {
            loadFromXml(inputStream);
        } catch(Exception e) {
            throw new TrackLoadingException(e);
        }
    }

    public Track(File file) throws TrackLoadingException {
        try {
            loadFromXml(new FileInputStream(file));
        } catch(Exception e) {
            throw new TrackLoadingException(e);
        }
    }

    public void insertSection(Tile tile, int position) {
        sections.add(position, new TrackSection(tile));
    }

    public void appendSection(Tile tile) {
        sections.add(new TrackSection(tile));
    }

    public void removeSection(int position) {
        throw new RuntimeException("Not yet implemented.");
    }

    public List<TrackSection> getSections() {
        return sections;
    }

    public String getTrackName() {
        return trackName;
    }

    public void saveToXml(String filename) {
        throw new RuntimeException("Not yet implemented.");
    }

    private Document parseTrackFile(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = null;
        domBuilder = domFactory.newDocumentBuilder();
        return domBuilder.parse(is);
    }

    private void loadFromXml(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        TilesetRepository tilesetRepository = (TilesetRepository) ContextLoader.getBean("tilesetRepository");

        // parse the XML file
        Document document = parseTrackFile(inputStream);
        trackName = document.getDocumentElement().getAttribute("tileset");
        String tilesetID = document.getDocumentElement().getAttribute("tileset");
        TileSet tileset = TileSet.valueOf(tilesetID.toUpperCase());

        // read the sections
        sections.clear();
        NodeList sectionNodes = document.getElementsByTagName("section");
        for(int i = 0; i < sectionNodes.getLength(); i++) {
            Element sectionElement = (Element) sectionNodes.item(i);
            String tileID = sectionElement.getAttribute("id");
            sections.add(new TrackSection(tilesetRepository.getTile(tileset, tileID)));
        }
    }
}
