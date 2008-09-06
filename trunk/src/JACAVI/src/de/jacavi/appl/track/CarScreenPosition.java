package de.jacavi.appl.track;

public class CarScreenPosition {
    public TrackSection section;

    public DirectedPoint point;

    public CarScreenPosition(TrackSection section, DirectedPoint point) {
        this.section = section;
        this.point = point;
    }
}
