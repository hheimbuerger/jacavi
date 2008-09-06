package de.jacavi.appl.track;

/**
 * Represents the position of a car during a race.
 */
public class CarPosition {
    /** The number of steps this car has already taken in its current lap. */
    public int stepsFromStart;

    /** The index of the lane this car is currently at. */
    public int currentLane;

    /** Whether the car is on a lane part that is used to change the lane */
    public boolean isOnLaneChange;

    /** Whether the car is still on track. If this is false, all other values but {@link lap} have undefined values. */
    public boolean isOnTrack;

    /** The number of laps this car has already completed. */
    public int lap;

    public void reset(int laneIndex) {
        stepsFromStart = 0;
        currentLane = laneIndex;
        isOnLaneChange = false;
        isOnTrack = true;
        lap = 0;
    }
}
