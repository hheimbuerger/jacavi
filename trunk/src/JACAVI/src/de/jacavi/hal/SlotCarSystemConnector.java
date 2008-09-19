package de.jacavi.hal;

import java.util.UUID;

import de.jacavi.rcp.util.Check;



/**
 * Adapts all the functionality to control and get feedback of a car during race.
 * <p>
 * {@link SlotCarSystemConnector} holds an {@link SlotCarSystemDriveConnector} and an {@link SlotCarFeedbackConnector}
 * and delegates to them.
 * <p>
 * {@link SlotCarSystemConnector}s are created by {@link SlotCarSystemConnectorFactory} and managed by
 * {@link ConnectorConfigurationManager}
 */
public class SlotCarSystemConnector implements SlotCarFeedbackConnector, SlotCarDriveConnector,
        Comparable<SlotCarSystemConnector> {

    private final UUID id;

    private final String name;

    private SlotCarDriveConnector driveConnector = null;

    private SlotCarFeedbackConnector feedbackConnector = null;

    public SlotCarSystemConnector(String name, SlotCarDriveConnector driveConnector,
            SlotCarFeedbackConnector feedbackConnector) {
        super();
        Check.Require(driveConnector != null && feedbackConnector != null,
                "driveConnector and feedbackConnector and tda may not be null");
        this.id = UUID.randomUUID();
        this.name = name;
        this.driveConnector = driveConnector;
        this.feedbackConnector = feedbackConnector;
    }

    public SlotCarDriveConnector getDriveConnector() {
        return driveConnector;
    }

    public SlotCarFeedbackConnector getFeedbackConnector() {
        return feedbackConnector;
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
