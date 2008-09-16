package de.jacavi.appl.track;

import java.awt.Point;



public class Checkpoint {

    private final String id;

    private final Point point;

    public Checkpoint(String id, Point point) {
        this.id = id;
        this.point = point;
    }

    public String getId() {
        return id;
    }

    public Point getPoint() {
        return point;
    }

}
