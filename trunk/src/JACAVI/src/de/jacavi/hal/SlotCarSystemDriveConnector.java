package de.jacavi.hal;

/*
 * fro: this interface should be as simple as possible to control a car for all the several hardware
 */
public interface SlotCarSystemDriveConnector {

    void setSpeed(int carID, int speed);

    int getSpeed(int carID);

    int getSwitch(int carID);

    int toggleSwitch(int carID);

    void fullBreak(int carID);

    // FeedbackSignal pollFeedback();

}
