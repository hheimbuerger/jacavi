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
    public void fullBreak(int carID) {
        driveConnector.fullBreak(carID);

    }

    @Override
    public int getSpeed(int carID) {
        return driveConnector.getSpeed(carID);
    }

    @Override
    public int getSwitch(int carID) {
        return driveConnector.getSwitch(carID);
    }

    @Override
    public void setSpeed(int carID, int speed) {
        driveConnector.setSpeed(carID, speed);
    }

    @Override
    public int toggleSwitch(int carID) {
        return driveConnector.toggleSwitch(carID);
    }

}
