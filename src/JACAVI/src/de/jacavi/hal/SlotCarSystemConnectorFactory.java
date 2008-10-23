package de.jacavi.hal;

import java.net.InetSocketAddress;

import de.jacavi.hal.analogue.AnalogueDriveConnectorAdapter;
import de.jacavi.hal.analogue.AnalogueFeedbackConnectorAdapter;
import de.jacavi.hal.bluerider.BlueriderDriveConnectorAdapter;
import de.jacavi.hal.bluerider.BlueriderFeedbackConnectorAdapter;
import de.jacavi.hal.lib42.Lib42DriveConnectorAdapter;
import de.jacavi.hal.lib42.Lib42FeedbackConnectorAdapter;
import de.jacavi.hal.simulation.SimulationDriveConnectorAdapter;
import de.jacavi.hal.simulation.SimulationFeedbackConnectorAdapter;



/**
 * Use this to create families of SlotCarSystemConnectors. <p> To create own new {@link SlotCarSystemConnector}s
 * implement there creation in this factory.
 */
public class SlotCarSystemConnectorFactory implements ConnectorFactory {

    /**
     * Creates a new SlotCarSystemConnector for lib42.
     */
    public SlotCarSystemConnector createLib42Connector(String name, int carID) throws Exception {
        return new SlotCarSystemConnector(name, new Lib42DriveConnectorAdapter(carID),
                new Lib42FeedbackConnectorAdapter(carID));
    }

    /**
     * Creates a new SlotCarSystemConnector for Bluerider. TODO: [ticket #10] implement the AnalogueFeedbackConnector
     */
    public SlotCarSystemConnector createBlueriderConnector(String name, String comPort,
            InetSocketAddress analogueDeviceAdress) {
        // Blueride can only run on analogue track so he can use the analogue light barrier sensor detection
        return new SlotCarSystemConnector(name, new BlueriderDriveConnectorAdapter(comPort),
                new BlueriderFeedbackConnectorAdapter(new AnalogueFeedbackConnectorAdapter(analogueDeviceAdress)));
    }

    /**
     * Creates an SlotCarSystemConnector for the Analogue technologie. <p> TODO: [ticket #10] implement
     * AnalogueDriveConnectorAdapter and AnalogueFeedbackConnectorAdapter Currently this function returns always null.
     */
    public SlotCarSystemConnector createAnalogueConnector(String name, int lane, InetSocketAddress analogueDeviceAdress) {
        return new SlotCarSystemConnector(name,new AnalogueDriveConnectorAdapter(lane,analogueDeviceAdress),new AnalogueFeedbackConnectorAdapter(analogueDeviceAdress));
    }

    /**
     * Creates a SlotCarSystemConnector for Simulation mode. You can say this connector is a dummy. Because there exists
     * no hardware.
     */
    public SlotCarSystemConnector createSimulatedConnector(String name) {
        return new SlotCarSystemConnector(name, new SimulationDriveConnectorAdapter(name),
                new SimulationFeedbackConnectorAdapter(name));
    }
}
