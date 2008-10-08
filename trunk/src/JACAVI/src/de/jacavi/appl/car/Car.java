package de.jacavi.appl.car;

import java.io.IOException;
import java.util.List;

import de.jacavi.appl.track.Tileset;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.util.MultiStyleImage;



public class Car {
    private final String CAR_BITMAP_PATH_PREFIX = "/cars/";

    private final String name;

    private final String filename;

    private final List<Tileset> tilesets;

    private final double acceleration;

    private final double mass;

    private final double topSpeed;

    private final double inertia;

    private final MultiStyleImage image;

    private final org.eclipse.swt.graphics.Image swtImage;

    public Car(String name, String filename, List<Tileset> tilesets, double acceleration, double mass, double topSpeed,
            double inertia) throws IOException {
        this.name = name;
        this.filename = filename;
        this.tilesets = tilesets;
        this.acceleration = acceleration;
        this.inertia = inertia;
        this.mass = mass;
        this.topSpeed = topSpeed;

        image = new MultiStyleImage(CAR_BITMAP_PATH_PREFIX + filename);

        // FIXME: this image is never disposed again!
        swtImage = Activator.getImageDescriptor(CAR_BITMAP_PATH_PREFIX + filename).createImage();
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public List<Tileset> getTilesets() {
        return tilesets;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getMass() {
        return mass;
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public double getInertia() {
        return inertia;
    }

    public MultiStyleImage getImage() {
        return image;
    }

    public org.eclipse.swt.graphics.Image getSwtImage() {
        return swtImage;
    }

    @Override
    public String toString() {
        return name;
    }

}