package de.jacavi.appl.valueobjects;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Point;

import de.jacavi.rcp.Activator;



public class Tile {

    private final BufferedImage sectionImage;

    private final Point entryPoint;

    private final Point exitPoint;

    private final Angle entryToExitAngle;

    public Tile(String filename, Point entryPoint, Point exitPoint, Angle entryToExitAngle) {
        // HACK: these images are not properly disposed yet
        // sectionImage = Activator.getImageDescriptor("/tracksections/" + filename).createImage();
        try {
            // sectionImage = ImageIO.read(new File("tracksections/" + filename));
            sectionImage = ImageIO.read(Activator.getResourceAsStream("/tracksections/" + filename));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
        this.entryToExitAngle = entryToExitAngle;
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
