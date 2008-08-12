package de.jacavi.appl.track;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Point;

import de.jacavi.appl.valueobjects.Angle;
import de.jacavi.rcp.Activator;



public class Tile {

    private final BufferedImage sectionImage;

    private final Point entryPoint;

    private final Point exitPoint;

    private final Angle entryToExitAngle;

    public Tile(String filename, Point entryPoint, Point exitPoint, int entryToExitAngle) throws IOException {
        sectionImage = ImageIO.read(Activator.getResourceAsStream(filename));

        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
        this.entryToExitAngle = new Angle(entryToExitAngle);
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

}
