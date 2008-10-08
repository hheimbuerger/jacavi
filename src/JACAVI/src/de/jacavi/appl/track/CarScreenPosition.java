package de.jacavi.appl.track;

public class CarScreenPosition {
    private final TrackSection section;

    private final DirectedPoint directedPoint;

    private final boolean isOnTrack;

    /**
     * Constructor for a car that is on-track.
     * 
     * @param section
     *            the section the car is at, may not be null
     * @param point
     *            the point
     * @param isOnTrack
     */
    public CarScreenPosition(TrackSection section, DirectedPoint point, boolean isOnTrack) {
        assert section != null;
        assert point != null;
        this.section = section;
        this.directedPoint = point;
        this.isOnTrack = isOnTrack;
    }

    public boolean isOnTrack() {
        return isOnTrack;
    }

    public TrackSection getSection() {
        return section;
    }

    public DirectedPoint getDirectedPoint() {
        return directedPoint;
    }

}
