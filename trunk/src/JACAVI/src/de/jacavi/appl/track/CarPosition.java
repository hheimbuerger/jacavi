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

    /** The index of the lane this car is currently at. */
    public int laneIndex;

    /** The number of steps this car has already taken in its current tile. */
    public int stepsOnTile;

    /** Whether the car is on a lane part that is used to change the lane. */
    public boolean isOnLaneChange;

    /** Whether the car is still on track. If this is false, all other values but {@link lap} have undefined values. */
    public boolean isOnTrack;

    /** The number of laps this car has already completed. */
    public int lap = 0;

    /** The player that holds this instance */
    private final Player player;

    private final RaceStatisticsManager raceStatisticsManager;

    public CarPosition(Player p) {
        this.player = p;
        raceStatisticsManager = (RaceStatisticsManager) ContextLoader.getBean("statisticsRegistryBean");
    }

    /**
     * Resets the car to the race starting point.
     */
    public void reset(StartingPoint startingPoint) {
        trackSectionIndex = 0;
        stepsOnTile = startingPoint.getSteps();
        laneIndex = startingPoint.getLaneIndex();
        isOnLaneChange = false;
        isOnTrack = true;
    }

    /**
     * Sets this car position to the given static position.
     */
    public void setPosition(int trackSectionIndex, int laneIndex, int stepsInTile, boolean incrementLaps) {
        this.isOnTrack = true;
        this.trackSectionIndex = trackSectionIndex;
        this.laneIndex = laneIndex;
        this.stepsOnTile = stepsInTile;
        if(incrementLaps) {
            lap++;
            raceStatisticsManager.fireLapCompleted(player);
        }
    }

    /**
     * Moves the car position by the given number of steps, taking all other aspects into account, like tile borders,
     * lane switching, etc.
     */
    public void moveSteps(Track track, int stepsToMove, boolean laneChangeTriggered) {
        assert isOnTrack: "Can't move a car that has left the track.";
        int remainingSteps = stepsToMove;

        while(remainingSteps > 0) {
            remainingSteps = moveOverTileSection(track, remainingSteps, laneChangeTriggered, track.getSections().get(
                    trackSectionIndex).getTile());
        }
    }

    /**
     * Moves the car over one tile section, where a tile section is here considered the <common>, <regular> or <change>
     * part of a tile.
     */
    private int moveOverTileSection(Track track, int stepsToMove, boolean laneChangeTriggered, Tile currentTile) {
        int remainingSteps = stepsToMove;

        // determine the current lane
        Lane lane = currentTile.getLane(laneIndex);

        // determine the current section (<common>, <regular> or <change>)
        int commonSteps = lane.getLaneSectionsCommon().getLength();
        LaneSectionList currentLaneSection;
        int remainingStepsInSection;
        boolean isInSecondSection;
        if(stepsOnTile < commonSteps) { // we're still in the <common> section
            currentLaneSection = lane.getLaneSectionsCommon();
            remainingStepsInSection = commonSteps - stepsOnTile;
            isInSecondSection = false;
        } else { // we're in the <change> or <regular> section
            currentLaneSection = isOnLaneChange ? lane.getLaneSectionsChange() : lane.getLaneSectionsRegular();
            remainingStepsInSection = currentLaneSection.getLength() + commonSteps - stepsOnTile;
            isInSecondSection = true;
        }

        // determine how many steps we can move on the current section and update stepsOnTile and remainingSteps
        int stepsToMoveInThisSection = Math.min(remainingSteps, remainingStepsInSection);
        stepsOnTile += stepsToMoveInThisSection;
        remainingSteps -= stepsToMoveInThisSection;

        // proceed to the second section if appropriate
        if(!isInSecondSection && stepsOnTile >= commonSteps) {
            isInSecondSection = true;
            if(laneChangeTriggered)
                isOnLaneChange = true;
        }

        // move on to the next tile if this one is finished
        int totalStepsOnTile = commonSteps
                + (isOnLaneChange ? lane.getLaneSectionsChange().getLength() : lane.getLaneSectionsRegular()
                        .getLength());
        assert stepsOnTile <= totalStepsOnTile;
        if(stepsOnTile == totalStepsOnTile) {
            // update the steps, the lane index, the track section index and the lane change state for the next tile
            stepsOnTile = 0;
            laneIndex = isOnLaneChange ? lane.getChangeExitLaneIndex() : lane.getRegularExitLaneIndex();
            trackSectionIndex++;
            isOnLaneChange = false;

            // if this was the last tile of the track, also move on to the next lap
            assert trackSectionIndex <= track.getSections().size();
            if(trackSectionIndex == track.getSections().size()) {
                trackSectionIndex = 0;
                lap++;
                raceStatisticsManager.fireLapCompleted(player);
            }
        }

        return remainingSteps;
    }

    /**
     * Moves the car off the track.
     */
    public void leaveTrack() {
        isOnTrack = false;
        /* trackSectionIndex = 0;
        laneIndex = 0;
        stepsOnTile = 0;
        isOnLaneChange = false; */
    }

    @Override
    public String toString() {
        if(isOnTrack)
            return "CarPosition[trackSectionIndex=" + trackSectionIndex + ", laneIndex=" + laneIndex + ", stepsOnTile="
                    + stepsOnTile + ", lap=" + lap + ", isOnLaneChange=" + isOnLaneChange + ", isOnTrack=" + isOnTrack
                    + "]";
        else
            return "CarPosition[left track]";
    }
}
