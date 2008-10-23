package de.jacavi.hal.analogue;

import java.net.InetSocketAddress;

import de.jacavi.hal.SlotCarDriveConnector;



public interface AnalogueDriveConnector extends SlotCarDriveConnector {

    int getLane();

    InetSocketAddress getAdress();

    boolean connect();
    
}
