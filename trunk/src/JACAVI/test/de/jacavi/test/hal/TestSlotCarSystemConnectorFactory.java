package de.jacavi.test.hal;

import java.net.InetSocketAddress;

import de.jacavi.hal.ConnectorFactory;
import de.jacavi.hal.SlotCarSystemConnector;
import de.jacavi.hal.simulation.SimulationFeedbackConnectorAdapter;
import de.jacavi.test.hal.connectors.TestAnalogueDriveConnectorAdapter;
import de.jacavi.test.hal.connectors.TestAnalogueFeddbackConnectorAdapter;
import de.jacavi.test.hal.connectors.TestBlueriderDriveConnectorAdapter;
import de.jacavi.test.hal.connectors.TestBlueriderFeedbackConnectorAdapter;
import de.jacavi.test.hal.connectors.TestLib42DriveConnectorAdapter;
import de.jacavi.test.hal.connectors.TestSimulationDriveConnectorAdapter;
import de.jacavi.test.hal.connectors.Testlib42FeedbackConnectorAdapter;



public class TestSlotCarSystemConnectorFactory implements ConnectorFactory {

    public SlotCarSystemConnector createLib42Connector(String name, int carID) {
        return new SlotCarSystemConnector(name, new TestLib42DriveConnectorAdapter(carID),
                new Testlib42FeedbackConnectorAdapter(carID));
    }

    public SlotCarSystemConnector createBlueriderConnector(String name, String comPort,
            InetSocketAddress analogueDeviceAdress) {
        return new SlotCarSystemConnector(name, new TestBlueriderDriveConnectorAdapter(comPort),
                new TestBlueriderFeedbackConnectorAdapter(analogueDeviceAdress));
    }

    public SlotCarSystemConnector createAnalogueConnector(String name, int lane, InetSocketAddress analogueDeviceAdress) {
        return new SlotCarSystemConnector(name, new TestAnalogueDriveConnectorAdapter(analogueDeviceAdress, lane),
                new TestAnalogueFeddbackConnectorAdapter(analogueDeviceAdress, lane));
    }

    public SlotCarSystemConnector createSimulatedConnector(String name) {
        return new SlotCarSystemConnector(name, new TestSimulationDriveConnectorAdapter(name),
                new SimulationFeedbackConnectorAdapter(name));
    }
}
