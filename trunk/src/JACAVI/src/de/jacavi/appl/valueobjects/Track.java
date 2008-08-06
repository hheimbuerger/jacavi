package de.jacavi.appl.valueobjects;

import java.util.LinkedList;
import java.util.List;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.valueobjects.TilesetRepository.TileSet;



public class Track {

    private List<TrackSection> sections = new LinkedList<TrackSection>();

    public Track() {
        TilesetRepository tilesetRepository = (TilesetRepository) ContextLoader.getBean("tilesetRepository");
        sections.add(new TrackSection(tilesetRepository.getTile(TileSet.DEBUG, "finishingStraight")));
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

    public void saveToXml(String filename) {
        throw new RuntimeException("Not yet implemented.");
    }

    public void loadFromXml(String filename) {
        // TODO: invoke from constructor and make private?
        throw new RuntimeException("Not yet implemented.");
    }

}
