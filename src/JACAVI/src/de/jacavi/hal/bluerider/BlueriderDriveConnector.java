package de.jacavi.hal.bluerider;

import de.jacavi.hal.SlotCarSystemDriveConnector;



public interface BlueriderDriveConnector extends SlotCarSystemDriveConnector {

    boolean connectBlueRider();

    boolean isConnected();

}
