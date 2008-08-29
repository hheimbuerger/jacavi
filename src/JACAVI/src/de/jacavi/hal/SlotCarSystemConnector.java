package de.jacavi.hal;

import de.jacavi.rcp.util.Check;



public class SlotCarSystemConnector implements SlotCarFeedbackConnector, SlotCarSystemDriveConnector {

    private SlotCarSystemDriveConnector driveConnector = null;

    private SlotCarFeedbackConnector feedbackConnector = null;

    public SlotCarSystemConnector(SlotCarSystemDriveConnector driveConnector, SlotCarFeedbackConnector feedbackConnector) {
        super();
        Check.Require(driveConnector != null && feedbackConnector != null,
                "driveConnector and feedbackConnector may not be null");
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

}
