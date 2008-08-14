package de.jacavi.appl.track;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Point;

import de.jacavi.rcp.Activator;



public class Tile {

    private final String filename;

    private final String name;

    private final BufferedImage sectionImage;

    private final Point entryPoint;

    private final Point exitPoint;

    private final Angle entryToExitAngle;

    private final boolean isInitial;

    public Tile(String filename, String name, boolean isInitial, Point entryPoint, Point exitPoint, int entryToExitAngle)
            throws IOException {
        sectionImage = ImageIO.read(Activator.getResourceAsStream(filename));

        this.filename = filename;
        this.name = name;
        this.isInitial = isInitial;
        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
        this.entryToExitAngle = new Angle(entryToExitAngle);
    }

    public String getFilename() {
        return filename;
    }

    public BufferedImage getSectionImage() {
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

    public String getName() {
        return name;
    }

    public boolean isInitial() {
        return isInitial;
    }

}
