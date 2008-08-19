package de.jacavi.appl.track;

import de.jacavi.appl.track.SlotPart.DirectedPoint;



public class TrackPosition {
    public TrackSection section;

    public DirectedPoint point;

    public TrackPosition(TrackSection section, DirectedPoint point) {
        this.section = section;
        this.point = point;
    }
}
