package de.jacavi.appl.track;

import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.Point;



public class TrackSection {

    private final Tile tile;

    public TrackSection(Tile tile) {
        this.tile = tile;
    }

    public Angle getEntryToExitAngle() {
        return tile.getEntryToExitAngle();
    }

    public Point getEntryPoint() {
        return tile.getEntryPoint();
    }

    public Point getExitPoint() {
        return tile.getExitPoint();
    }

    public BufferedImage getImage() {
        return tile.getSectionImage();
    }

    public boolean isInitial() {
        return tile.isInitial();
    }

    public Slot getSlot1() {
        return tile.getSlot1();
    }

    public Slot getSlot2() {
        return tile.getSlot2();
    }

    public String getName() {
        return tile.getName();
    }

    public Tile getTile() {
        return tile;
    }

}
