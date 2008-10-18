package de.jacavi.appl.track;

public class StartingPoint {
    private final int laneIndex;

    private final int steps;

    private final boolean hasTrackInformation;

    private final int trackSectionIndex;

    public StartingPoint(int laneIndex, int steps) {
        this.laneIndex = laneIndex;
        this.steps = steps;
        this.hasTrackInformation = false;
        this.trackSectionIndex = -1;
    }

    public StartingPoint(StartingPoint sp, int trackSectionIndex) {
        this.laneIndex = sp.laneIndex;
        this.steps = sp.steps;
        this.hasTrackInformation = true;
        this.trackSectionIndex = trackSectionIndex;
    }

    public int getLaneIndex() {
        return laneIndex;
    }

    public int getSteps() {
        return steps;
    }

    public int getTrackSectionIndex() {
        assert hasTrackInformation: "getTrackSectionIndex() may not be invoked on a generic StartingPoint that is lacking any track information.";
        return trackSectionIndex;
    }

}
