package de.jacavi.appl.track;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Point;

import de.jacavi.rcp.util.MultiStyleImage;



public class Tile {

    private final String id;

    private final File bitmapFile;

    private final String name;

    private final MultiStyleImage sectionImage;

    private final Point entryPoint;

    private final Point exitPoint;

    private final Angle entryToExitAngle;

    private final boolean isInitial;

    private final Lane[] lanes;

    private final StartingPoint[] startingPoints;

    public Tile(String id, File file, String name, boolean isInitial, Point entryPoint, Point exitPoint,
            int entryToExitAngle, Lane[] lanes, StartingPoint[] startingPoints) throws IOException {
        this.id = id;
        this.bitmapFile = file;
        this.name = name;
        this.isInitial = isInitial;
        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
        this.entryToExitAngle = new Angle(entryToExitAngle);
        this.lanes = lanes;
        this.startingPoints = startingPoints;

        sectionImage = new MultiStyleImage(file);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public File getBitmapFile() {
        return bitmapFile;
    }

    public List<Lane> getLanes() {
        return Arrays.asList(lanes);
    }

    public MultiStyleImage getSectionImage() {
        return sectionImage;
    }

    public Point getEntryPoint() {
        return entryPoint;
    }

    public Point getExitPoint() {
        return exitPoint;
    }

    public Angle getEntryToExitAngle() {
        return entryToExitAngle;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public Lane getLane(int index) {
        return lanes[index];
    }

    public int getLaneCount() {
        return lanes.length;
    }

    public StartingPoint[] getStartingPoints() {
        return startingPoints;
    }

}
