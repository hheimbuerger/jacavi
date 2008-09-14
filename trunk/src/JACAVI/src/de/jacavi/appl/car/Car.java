package de.jacavi.appl.car;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import de.jacavi.appl.track.Tileset;
import de.jacavi.rcp.Activator;



public class Car {
    private final String CAR_BITMAP_PATH_PREFIX = "/cars/";

    private final String filename;

    private final List<Tileset> tilesets;

    private final int acceleration;

    private final int mass;

    private final int topSpeed;

    private final int inertia;

    private final Image image;

    public Car(String filename, List<Tileset> tilesets, int acceleration, int inertia, int mass, int topSpeed)
            throws IOException {
        this.filename = filename;
        this.tilesets = tilesets;
        this.acceleration = acceleration;
        this.inertia = inertia;
        this.mass = mass;
        this.topSpeed = topSpeed;

        InputStream resourceAsStream = Activator.getResourceAsStream(CAR_BITMAP_PATH_PREFIX + filename);
        if(resourceAsStream == null)
            throw new IllegalArgumentException("The image resource " + CAR_BITMAP_PATH_PREFIX + filename
                    + " could not be loaded.");
        image = ImageIO.read(resourceAsStream);
    }

    public String getFilename() {
        return filename;
    }

    public List<Tileset> getTilesets() {
        return tilesets;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public int getMass() {
        return mass;
    }

    public int getTopSpeed() {
        return topSpeed;
    }

    public int getInertia() {
        return inertia;
    }

    public Image getImage() {
        return image;
    }
}
