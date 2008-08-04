package de.jacavi.appl.valueobjects;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Point;



public class Track {

    private List<TrackSection> sections = new LinkedList<TrackSection>();

    public Track() {
        sections.add(new TrackSection("finishing_straight.png", new Point(106, 0), 0));
    }

    public void insertSection(TrackSection section, int position) {
        sections.add(position, section);
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
