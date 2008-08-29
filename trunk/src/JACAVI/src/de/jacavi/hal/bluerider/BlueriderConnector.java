package de.jacavi.hal.bluerider;

import de.jacavi.hal.SlotCarSystemDriveConnector;



public interface BlueriderConnector extends SlotCarSystemDriveConnector {

    public void connectBlueRider(String comPort);
}
