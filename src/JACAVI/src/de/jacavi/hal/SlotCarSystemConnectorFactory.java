package de.jacavi.hal;

import java.net.InetSocketAddress;

import de.jacavi.appl.racelogic.tda.BlueriderTDA;
import de.jacavi.appl.racelogic.tda.DebugTDA;
import de.jacavi.appl.racelogic.tda.Lib42TDA;
import de.jacavi.hal.analogue.AnalogueFeedbackConnectorAdapter;
import de.jacavi.hal.bluerider.BlueriderDriveConnectorAdapter;
import de.jacavi.hal.bluerider.BlueriderFeedbackConnectorAdapter;
import de.jacavi.hal.lib42.Lib42DriveConnectorAdapter;
import de.jacavi.hal.lib42.Lib42FeedbackConnectorAdapter;
import de.jacavi.hal.simulation.SimulationDriveConnectorAdapter;
import de.jacavi.hal.simulation.SimulationFeedbackConnectorAdapter;



/**
 * Use this to create families of SlotCarSystemConnectors.
 * <p>
 * To create own new {@link SlotCarSystemConnector}s implement there creation in this factory.
 */
public class SlotCarSystemConnectorFactory implements ConnectorFactory {

    public SlotCarSystemConnector createLib42Connector(String name, int carID) {
        return new SlotCarSystemConnector(name, new Lib42DriveConnectorAdapter(carID),
                new Lib42FeedbackConnectorAdapter(carID), new Lib42TDA());
    }

    public SlotCarSystemConnector createBlueriderConnector(String name, String comPort,
            InetSocketAddress analogueDeviceAdress) {
        // Blueride can only run on analogue track so he can use the analogue light barrier sensor detection
        return new SlotCarSystemConnector(name, new BlueriderDriveConnectorAdapter(comPort),
                new BlueriderFeedbackConnectorAdapter(new AnalogueFeedbackConnectorAdapter(analogueDeviceAdress)),
                new BlueriderTDA());
    }

    public SlotCarSystemConnector createAnalogueConnector(String name, int lane, InetSocketAddress analogueDeviceAdress) {
        // TODO:
        return null;
    }

    public SlotCarSystemConnector createSimulatedConnector(String name) {
        // TODO:
        return new SlotCarSystemConnector(name, new SimulationDriveConnectorAdapter(name),
                new SimulationFeedbackConnectorAdapter(name), new DebugTDA());
    }
}
