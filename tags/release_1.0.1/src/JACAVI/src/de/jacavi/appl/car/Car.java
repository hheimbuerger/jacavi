package de.jacavi.appl.car;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.jacavi.appl.track.Tileset;
import de.jacavi.rcp.util.MultiStyleImage;



public class Car {

    private final String name;

    private final File bitmapFile;

    private final List<Tileset> tilesets;

    private final double acceleration;

    private final double mass;

    private final double topSpeed;

    private final double inertia;

    private final MultiStyleImage image;

    private final org.eclipse.swt.graphics.Image swtImage;

    public Car(String name, File bitmapFile, List<Tileset> tilesets, double acceleration, double mass, double topSpeed,
            double inertia) throws IOException {
        this.name = name;
        this.bitmapFile = bitmapFile;
        this.tilesets = tilesets;
        this.acceleration = acceleration;
        this.inertia = inertia;
        this.mass = mass;
        this.topSpeed = topSpeed;

        image = new MultiStyleImage(bitmapFile);

        // TODO: [ticket #161] this image is never disposed again!
        swtImage = new Image(Display.getDefault(), new FileInputStream(bitmapFile));
    }

    public String getName() {
        return name;
    }

    public File getBitmapFile() {
        return bitmapFile;
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
