package de.jacavi.hal.bluerider;

import de.jacavi.hal.SlotCarDriveConnector;



public interface BlueriderDriveConnector extends SlotCarDriveConnector {

    boolean connectBlueRider();

    boolean isConnected();

}
