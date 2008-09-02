package de.jacavi.hal.bluerider;

import de.jacavi.hal.SlotCarSystemDriveConnector;



public interface BlueriderConnector extends SlotCarSystemDriveConnector {

    boolean connectBlueRider();

    boolean isConnected();
}
