package de.jacavi.hal;

/**
 * This interface describes the main functions all cars should support.
 * <p>
 * If they don't leaf implementation empty.
 */
public interface SlotCarDriveConnector {

    void setThrust(int thrust);

    int getThrust();

    boolean getSwitch();

    boolean toggleSwitch();

    void fullBreak();

    void switchFrontLight();

    void switchBackLight();

    boolean isFrontLightOn();

    boolean isBackLightOn();
}
