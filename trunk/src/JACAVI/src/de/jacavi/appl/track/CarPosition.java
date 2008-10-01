package de.jacavi.appl.track;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.racelogic.RaceStatisticsManager;



/**
 * Represents the position of a car during a race.
 */
public class CarPosition {
    /** The section of the track the car is currently at. */
    public int trackSectionIndex;

    /** The number of steps this car has already taken in its current tile. */
    public int stepsInTile;

    /** The index of the lane this car is currently at. */
    public int laneIndex;

    /** Whether the car is on a lane part that is used to change the lane. */
    public boolean isOnLaneChange;

    /**
     * Whether the car is still on track. If this is false, all other values but {@link lap} have undefined values.
     */
    public boolean isOnTrack;

    /** The number of laps this car has already completed. */
    public int lap;

    /** The player that holds this instance */
    public Player player;

    private final RaceStatisticsManager raceStatisticsManager;

    public CarPosition(Player p) {
        this.player = p;
        raceStatisticsManager = (RaceStatisticsManager) ContextLoader.getBean("statisticsRegistryBean");
    }

    public void reset(int laneIndex) {
        trackSectionIndex = 0;
        stepsInTile = 0;
        this.laneIndex = laneIndex;
        isOnLaneChange = false;
        isOnTrack = true;
        lap = 0;
    }

    public void moveSteps(Track track, int stepsToMove, boolean laneChangeTriggered) {
        int remainingSteps = stepsToMove;

        while(remainingSteps > 0) {
            remainingSteps = moveOverTile(track, remainingSteps, track.getSections().get(trackSectionIndex).getTile());
        }
    }

    public void setPosition(int trackSectionIndex, int laneIndex, int stepsInTile, boolean incrementLaps) {
        this.trackSectionIndex = trackSectionIndex;
        this.laneIndex = laneIndex;
        this.stepsInTile = stepsInTile;
        if(incrementLaps)
            lap++;
    }

    private int moveOverTile(Track track, int remainingSteps, Tile currentTile) {
        Lane lane = currentTile.getLane(laneIndex);
        LaneSectionList laneSectionsCommon = lane.getLaneSectionsCommon();

        int commonSteps = laneSectionsCommon.getLength();
        if(remainingSteps < commonSteps - stepsInTile) {
            stepsInTile += remainingSteps;
            return 0;
        } else {
            stepsInTile = 0;
            laneIndex = isOnLaneChange ? lane.getChangeExitLaneIndex() : lane.getRegularExitLaneIndex();
            trackSectionIndex++;
            if(trackSectionIndex >= track.getSections().size()) {
                trackSectionIndex = 0;
                lap++;
                raceStatisticsManager.fireLapCompleted(player);
            }
            return remainingSteps - (commonSteps - stepsInTile);
        }
    }
}
