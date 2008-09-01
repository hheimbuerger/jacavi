package de.jacavi.hal;

import java.net.InetSocketAddress;

import de.jacavi.hal.analogue.AnalogueFeedbackConnectorAdapter;
import de.jacavi.hal.bluerider.BlueriderDriveConnectorAdapter;
import de.jacavi.hal.lib42.Lib42DriveConnectorAdapter;
import de.jacavi.hal.lib42.Lib42FeedbackConnectorAdapter;
import de.jacavi.hal.simulation.SimulationDriveConnectorAdapter;
import de.jacavi.hal.simulation.SimulationFeddbackConnectorAdapter;



public class SlotCarSystemConnectorFactory implements ConnectorFactory {

    public SlotCarSystemConnector createLib42Connector(String name, int carID) {
        return new SlotCarSystemConnector(name, new Lib42DriveConnectorAdapter(carID),
                new Lib42FeedbackConnectorAdapter(carID));
    }

    public SlotCarSystemConnector createBlueriderConnector(String name, String comPort,
            InetSocketAddress analogueDeviceAdress) {
        // Blueride can only run on analogue track so he can use the analogue light barrier sensor detection
        // TODO:
        return new SlotCarSystemConnector(name, new BlueriderDriveConnectorAdapter(),
                new AnalogueFeedbackConnectorAdapter());
    }

    public SlotCarSystemConnector createAnalogueConnector(String name, int lane, InetSocketAddress analogueDeviceAdress) {
        // TODO:
        return null;
    }

    public SlotCarSystemConnector createSimulatedConnector(String name) {
        // TODO:
        return new SlotCarSystemConnector(name, new SimulationDriveConnectorAdapter(),
                new SimulationFeddbackConnectorAdapter());
    }
}
