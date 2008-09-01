package de.jacavi.hal.analogue;

import java.net.InetSocketAddress;

import de.jacavi.hal.SlotCarSystemDriveConnector;



public interface AnalogueDriveConnector extends SlotCarSystemDriveConnector {

    int getLane();

    InetSocketAddress getAdress();
}
