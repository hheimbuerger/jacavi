package de.jacavi.hal;

import java.net.InetSocketAddress;



/**
 * Interface for connector creation.
 */
public interface ConnectorFactory {

    public SlotCarSystemConnector createLib42Connector(String name, int carID);

    public SlotCarSystemConnector createBlueriderConnector(String name, String comPort,
            InetSocketAddress analogueDeviceAdress);

    public SlotCarSystemConnector createAnalogueConnector(String name, int lane, InetSocketAddress analogueDeviceAdress);

    public SlotCarSystemConnector createSimulatedConnector(String name);
}
