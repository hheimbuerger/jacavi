package de.jacavi.appl.track;

public class CarScreenPosition {
    public TrackSection section;

    public DirectedPoint directedPoint;

    public CarScreenPosition(TrackSection section, DirectedPoint point) {
        assert section != null;
        assert point != null;
        this.section = section;
        this.directedPoint = point;
    }
}
