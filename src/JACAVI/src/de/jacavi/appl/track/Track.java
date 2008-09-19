package de.jacavi.appl.track;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.jacavi.appl.ContextLoader;



/**
 * Represents a slot car track.
 * <p>
 * The track has a specific name, a certain tile set to be used and a list of sections (parametrised tiles).
 * <p>
 * While the name and the list of sections can be modified, the tile set is static.
 */
public class Track {

    /**
     * The exception that is thrown when loading a track fails. A more detailed explanation of the cause can be found in
     * the root exception.
     */
    @SuppressWarnings("serial")
    public class TrackLoadingException extends Exception {
        public TrackLoadingException(String message) {
            super(message);
        }

        public TrackLoadingException(Exception e) {
            super(e);
        }
    }

    /**
     * Thrown when an attempt to delete the finishing straight from the track is detected.
     */
    @SuppressWarnings("serial")
    public class InitialTileMayNotBeRemoved extends Exception {}

    /**
     * File Extension that is used for every track in the filesystem.
     */
    public static String FILE_EXTENSION = ".track.xml";

    /**
     * Interface to implement for receiving modification notifications.
     */
    public interface TrackModificationListener {
        void handleTrackModified();
    }

    /** The tile set used for this track. */
    private Tileset tileset;

    /** The list of sections (tiles) this track consists of. */
    private final List<TrackSection> sections = new LinkedList<TrackSection>();

    /** The visible name of the track. */
    private String trackName;

    /** The listeners receiving modification events. */
    private final List<TrackModificationListener> listeners;

    /**
     * Private constructor that is only called internally with common initializations.
     */
    private Track() {
        listeners = new ArrayList<TrackModificationListener>();
    }

    /**
     * Constructor
     * <p>
     * Creates a new, empty Track using a given TileSet.
     * 
     * @param tileset
     *            the tileset to be used for the track
     */
    public Track(Tileset tileset) {
        this();
        this.tileset = tileset;
        sections.add(new TrackSection(tileset.getInitialTile()));
    }

    /**
     * Constructor
     * <p>
     * Loads an existing track from a file, given as an input stream.
     * 
     * @param inputStream
     *            the input stream to read the file from
     * @throws TrackLoadingException
     *             raised when loading the track fails
     */
    public Track(InputStream inputStream) throws TrackLoadingException {
        this();
        try {
            loadFromXml(inputStream);
        } catch(Exception e) {
            throw new TrackLoadingException(e);
        }
    }

    /**
     * Constructor
     * <p>
     * Loads an existing track from a file, given as a file object.
     * 
     * @param file
     *            the file to load
     * @throws TrackLoadingException
     *             raised when loading the track fails
     * @throws FileNotFoundException
     *             raised if the file can't be found
     */
    public Track(File file) throws TrackLoadingException, FileNotFoundException {
        this(new FileInputStream(file));
    }

    public void addListener(TrackModificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TrackModificationListener listener) {
        listeners.remove(listener);
    }

    private void invokeListeners() {
        for(TrackModificationListener l: listeners)
            l.handleTrackModified();
    }

    public Tileset getTileset() {
        return tileset;
    }

    /**
     * Inserts a new section (tile) at the given position.
     * 
     * @param tile
     *            the tile to insert
     * @param position
     *            index at which the specified element is to be inserted
     * @throws IndexOutOfBoundsException
     *             raised if the position is invalid
     */
    public void insertSection(Tile tile, int position) throws IndexOutOfBoundsException {
        sections.add(position, new TrackSection(tile));
        invokeListeners();
    }

    /**
     * Inserts a new section (tile) at the end of the track (after the last section).
     * 
     * @param tile
     *            the tile to insert
     */
    public void appendSection(Tile tile) {
        sections.add(new TrackSection(tile));
        invokeListeners();
    }

    /**
     * Removes a section from the track.
     * <p>
     * All modification listeners are notified.
     * 
     * @param position
     *            index of the section to remove
     * @throws InitialTileMayNotBeRemoved
     *             if the tile at the given index is the initial tile
     * @throws IndexOutOfBoundsException
     *             if the position is invalid (valid: 0 <= position < sections.size())
     */
    public void removeSection(int position) throws InitialTileMayNotBeRemoved, IndexOutOfBoundsException {
        // precondition: throw an exception if position indicates the initial tile
        if(sections.get(position).isInitial())
            throw new InitialTileMayNotBeRemoved();

        // remove the tile at the given position (will throw IndexOutOfBoundsException if the position is invalid)
        sections.remove(position);

        // notify all listeners
        invokeListeners();
    }

    /*public int getLaneLength(int index) {
        int length = 0;
        for(TrackSection s: sections)
            for(LaneSection ls: s.getLane(index).getLaneSections())
                length += ls.length;
        return length;
    }*/

    public CarScreenPosition determineScreenPositionFromPosition(CarPosition position) {
        /*int length = 0;
        for(TrackSection s: sections) {
            Lane lane = s.getLane(position.currentLane);
            if(position.stepsFromStart < length + lane.getLength())
                return new CarScreenPosition(s, lane.getStepPoint(position.stepsFromStart - length));
            length += lane.getLength();
        }
        return null;*/
        TrackSection section = sections.get(position.trackSectionIndex);
        Lane lane = section.getLane(position.currentLane);
        return new CarScreenPosition(section, lane.getStepPoint(position));
    }

    public List<TrackSection> getSections() {
        return sections;
    }

    /**
     * Returns the visible name of the track.
     * 
     * @return the name of the track
     */
    public String getTrackName() {
        return trackName;
    }

    /**
     * Sets the name of the track.
     * 
     * @param trackName
     *            the new name of the track
     */
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    /**
     * Writes the current track to a file.
     * 
     * @param filename
     *            the file to save the track to
     * @throws (various exceptions) thrown when saving fails
     */
    public void saveToXml(String filename) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError,
            TransformerException, ParserConfigurationException {
        // create the FileOutputStream and the DocumentBuilder
        FileOutputStream fos = new FileOutputStream(filename);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        // create the document and the root element
        Document document = impl.createDocument(null, null, null);
        Element rootElement = document.createElement("track");
        rootElement.setAttribute("name", trackName);
        rootElement.setAttribute("tileset", tileset.toString().toLowerCase());
        document.appendChild(rootElement);

        // append child elements for all the track sections
        for(TrackSection ts: sections) {
            Element sectionElement = document.createElement("section");
            sectionElement.setAttribute("id", ts.getTile().getId());
            rootElement.appendChild(sectionElement);
        }

        // transform the Document into a String
        DOMSource domSource = new DOMSource(document);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, new StreamResult(fos));
    }

    private Document parseTrackFile(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = null;
        domBuilder = domFactory.newDocumentBuilder();
        return domBuilder.parse(is);
    }

    private void loadFromXml(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException,
            TrackLoadingException {
        TilesetRepository tilesetRepository = (TilesetRepository) ContextLoader.getBean("tilesetRepositoryBean");

        // parse the XML file
        Document document = parseTrackFile(inputStream);
        trackName = document.getDocumentElement().getAttribute("name");
        String tilesetID = document.getDocumentElement().getAttribute("tileset");
        tileset = tilesetRepository.getTileset(tilesetID);
        if(tileset == null)
            throw new TrackLoadingException("The track " + trackName + " uses the unknown tileset " + tilesetID + ".");

        // read the sections
        boolean containsInitial = false;
        sections.clear();
        NodeList sectionNodes = document.getElementsByTagName("section");
        for(int i = 0; i < sectionNodes.getLength(); i++) {
            Element sectionElement = (Element) sectionNodes.item(i);
            String tileID = sectionElement.getAttribute("id");
            Tile tile = tileset.getTile(tileID);
            if(tile == null)
                throw new TrackLoadingException("The track " + trackName + " referenced the unknown tile " + tileID
                        + " of tileset " + tilesetID + " at position " + i);
            if(tile.isInitial() && containsInitial)
                throw new TrackLoadingException("The track " + trackName + " contains two initial tiles.");
            else if(tile.isInitial())
                containsInitial = true;
            sections.add(new TrackSection(tile));
        }

        // make sure there's one initial tile in the track
        if(!containsInitial)
            throw new TrackLoadingException("The track " + trackName + " contains no initial tile.");
    }

    public TrackSection getInitialSection() {
        return sections.get(0);
    }
}
