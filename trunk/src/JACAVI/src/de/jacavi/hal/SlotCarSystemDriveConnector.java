package de.jacavi.hal;

/*
 * fro: this interface should be as simple as possible to control a car for all the several hardware
 */
public interface SlotCarSystemDriveConnector {

    void setSpeed(int speed);

    int getSpeed();

    int getSwitch();

    int toggleSwitch();

    void fullBreak();

}
