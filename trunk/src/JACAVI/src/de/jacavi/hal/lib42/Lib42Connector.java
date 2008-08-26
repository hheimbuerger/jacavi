package de.jacavi.hal.lib42;

import de.jacavi.hal.SlotCarSystemConnector;



/*
 * This interface has extra Clib42 functionality for example to program a cars id
 */
public interface Lib42Connector extends SlotCarSystemConnector {
    // HANDLE PIT STOP
    void setPitstop(int carId, int pitstop);

    int togglePitstop(int carId);

    int getPitstop(int carId);

    // HANDLE FUEL FOR ALL CARS
    void activateFuel();

    void deactivateFuel();

    /*
     * Methods to programm cars
     */
    void programmCar(int carID);

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

    int isPacecarActive();

    int getPcSwitch();
}
