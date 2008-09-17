package de.jacavi.hal;

/**
 * This interface describes the main functions all cars should support.
 * <p>
 * If they don't leaf implementation empty.
 */
public interface SlotCarDriveConnector {

    void setSpeed(int speed);

    int getSpeed();

    int getSwitch();

    int toggleSwitch();

    void fullBreak();

    void switchFrontLight();

    void switchBackLight();

    boolean isFrontLightOn();

    boolean isBackLightOn();
}
