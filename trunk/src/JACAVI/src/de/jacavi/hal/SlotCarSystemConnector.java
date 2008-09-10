package de.jacavi.hal;

import java.util.UUID;

import de.jacavi.rcp.util.Check;



public class SlotCarSystemConnector implements SlotCarFeedbackConnector, SlotCarSystemDriveConnector,
        Comparable<SlotCarSystemConnector> {

    private final UUID id;

    public SlotCarSystemDriveConnector getDriveConnector() {
        return driveConnector;
    }

    public SlotCarFeedbackConnector getFeedbackConnector() {
        return feedbackConnector;
    }

    private final String name;

    private SlotCarSystemDriveConnector driveConnector = null;

    private SlotCarFeedbackConnector feedbackConnector = null;

    public SlotCarSystemConnector(String name, SlotCarSystemDriveConnector driveConnector,
            SlotCarFeedbackConnector feedbackConnector) {
        super();
        Check.Require(driveConnector != null && feedbackConnector != null,
                "driveConnector and feedbackConnector may not be null");
        this.id = UUID.randomUUID();
        this.name = name;
        this.driveConnector = driveConnector;
        this.feedbackConnector = feedbackConnector;
    }

    @Override
    public FeedbackSignal pollFeedback() {
        return feedbackConnector.pollFeedback();
    }

    @Override
    public void fullBreak() {
        driveConnector.fullBreak();

    }

    @Override
    public int getSpeed() {
        return driveConnector.getSpeed();
    }

    @Override
    public int getSwitch() {
        return driveConnector.getSwitch();
    }

    @Override
    public void setSpeed(int speed) {
        driveConnector.setSpeed(speed);
    }

    @Override
    public int toggleSwitch() {
        return driveConnector.toggleSwitch();
    }

    @Override
    public int compareTo(SlotCarSystemConnector o) {
        return name.compareTo(o.getName());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void switchBackLight() {
        driveConnector.switchBackLight();

    }

    @Override
    public void switchFrontLight() {
        driveConnector.switchFrontLight();
    }

    @Override
    public boolean isBackLightOn() {
        return driveConnector.isBackLightOn();
    }

    @Override
    public boolean isFrontLightOn() {
        return driveConnector.isFrontLightOn();
    }

}
