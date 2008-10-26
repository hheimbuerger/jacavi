package de.jacavi.hal.lib42;

import de.jacavi.hal.SlotCarDriveConnector;



/*
 * This interface has extra Clib42 functionality for example to program a cars id
 */
public interface Lib42DriveConnector extends SlotCarDriveConnector {
    // HANDLE PIT STOP
    void setPitstop(int pitstop);

    int togglePitstop();

    int getPitstop();

    // HANDLE FUEL FOR ALL CARS
    void activateFuel();

    void deactivateFuel();

    /*
     * Methods to programm cars
     */
    void programmCar();

    void programmSpeed(int value);

    void programmBreak(int value);

    void programmFuel(int value);

    void resetCars();

    // Handle Pace Car
    void activatePacecar();

    void deactivatePacecar();

    void pacecar2box();

    void setPacecarSwitch(int value);

    int togglePacecarSwitch();

    void setPcPitstop(int pitstop);

    boolean isPacecarActive();

    int getPcSwitch();

    int getCarID();

}