package de.jacavi.appl.track;

import java.io.IOException;

import org.eclipse.swt.graphics.Point;

import de.jacavi.rcp.util.MultiStyleImage;



public class Tile {

    private final String filename;

    private final String name;

    private final MultiStyleImage sectionImage;

    private final Point entryPoint;

    private final Point exitPoint;

    private final Angle entryToExitAngle;

    private final boolean isInitial;

    private final Slot slot1;

    private final Slot slot2;

    private final String id;

    public Tile(String id, String filename, String name, boolean isInitial, Point entryPoint, Point exitPoint,
            int entryToExitAngle, Slot slot1, Slot slot2) throws IOException {
        this.id = id;
        this.filename = filename;
        this.name = name;
        this.isInitial = isInitial;
        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
        this.entryToExitAngle = new Angle(entryToExitAngle);
        this.slot1 = slot1;
        this.slot2 = slot2;

        sectionImage = new MultiStyleImage(filename);
    }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getName() {
        return name;
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

    public Slot getSlot1() {
        return slot1;
    }

    public Slot getSlot2() {
        return slot2;
    }

}
