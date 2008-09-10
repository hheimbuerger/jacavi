package de.jacavi.hal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.jacavi.appl.ContextLoader;
import de.jacavi.hal.analogue.AnalogueDriveConnector;
import de.jacavi.hal.analogue.AnalogueDriveConnectorAdapter;
import de.jacavi.hal.bluerider.BlueriderDriveConnectorAdapter;
import de.jacavi.hal.lib42.Lib42DriveConnector;
import de.jacavi.hal.lib42.Lib42DriveConnectorAdapter;
import de.jacavi.hal.simulation.SimulationDriveConnectorAdapter;
import de.jacavi.test.hal.connectors.TestAnalogueDriveConnectorAdapter;
import de.jacavi.test.hal.connectors.TestBlueriderDriveConnectorAdapter;
import de.jacavi.test.hal.connectors.TestLib42DriveConnectorAdapter;



/**
 * Represents the management of configured {@link SlotCarSystemConnector}s.
 * <p>
 * Every by {@link SlotCarSystemConnectorFactory} created {@link SlotCarSystemConnector} will be managed here drung his
 * life time.
 */
public class ConnectorConfigurationManager {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ConnectorConfigurationManager.class);

    private final Map<UUID, SlotCarSystemConnector> connectors = new TreeMap<UUID, SlotCarSystemConnector>();

    public ConnectorConfigurationManager() {
        logger.info("instantiating ConnectorConfigurationManager.");
        // create one simulated connector (dummy)
        // TODO: create three more if other players want to play simulated
        ConnectorFactory connectorFactory = (ConnectorFactory) ContextLoader.getBean("slotCarSystemConnectorFactory");
        SlotCarSystemConnector simulatedConnector = connectorFactory.createSimulatedConnector("Simulation connector");
        addConnector(simulatedConnector);
    }

    /**
     * adds an {@link SlotCarSystemConnector} to the managment.
     * 
     * @param connector
     */
    public void addConnector(SlotCarSystemConnector connector) {

        logger.info(connector.getName() + " is now under connector management");
        Class<?> c = connector.getDriveConnector().getClass();

        if((c == TestLib42DriveConnectorAdapter.class) || (c == Lib42DriveConnectorAdapter.class)) {
            reconnectLib42Connector(connector);
        } else if(c == BlueriderDriveConnectorAdapter.class || (c == TestBlueriderDriveConnectorAdapter.class)) {
            reconnectBlueriderConnector(connector);
        } else if(c == AnalogueDriveConnectorAdapter.class || (c == TestAnalogueDriveConnectorAdapter.class)) {
            reconnectAnalogueConnector(connector);
        } else if(c == SimulationDriveConnectorAdapter.class) {
            reconnectSimulationConnector(connector);
        }

        connectors.put(connector.getId(), connector);
    }

    /**
     * @return a sorted list of managed {@link SlotCarSystemConnector}s
     */
    public List<SlotCarSystemConnector> getConnectors() {
        List<SlotCarSystemConnector> result = new ArrayList<SlotCarSystemConnector>();
        for(SlotCarSystemConnector connector: connectors.values())
            result.add(connector);
        Collections.sort(result);
        return result;
    }

    /**
     * Removes the {@link SlotCarSystemConnector} by UUID
     * 
     * @param id
     */
    public void removeInputDevice(UUID id) {
        if(connectors.containsKey(id)) {
            connectors.remove(id);
        } else
            throw new IllegalArgumentException("tried removing an ID that does not exist!");

    }

    /**
     * @param type
     * @return a sorted list with {@link SlotCarSystemConnector}s with class type
     */
    public List<SlotCarSystemConnector> getByDriveConnectorType(Class<?> type) {
        List<SlotCarSystemConnector> result = new ArrayList<SlotCarSystemConnector>();
        for(SlotCarSystemConnector connector: connectors.values())
            if(connector.getDriveConnector().getClass() == type)
                result.add(connector);
        Collections.sort(result);
        return result;
    }

    /**
     * @param id
     * @return true if id is valid otherwise false
     */
    public boolean isIdValid(UUID id) {
        return connectors.containsKey(id);
    }

    /**
     * @param id
     * @return the connector with the given id
     */
    public SlotCarSystemConnector getConnector(UUID id) {
        return connectors.get(id);
    }

    private void reconnectSimulationConnector(SlotCarSystemConnector simulationConnector) {

    // TODO:
    }

    /**
     * Removes an on the same lane existing analogue connector.
     * 
     * @param analogueConnector
     */
    private void reconnectAnalogueConnector(SlotCarSystemConnector analogueConnector) {
        // get the test adapters
        List<SlotCarSystemConnector> list = getByDriveConnectorType(TestAnalogueDriveConnectorAdapter.class);
        // get the real adapters
        list.addAll(getByDriveConnectorType(AnalogueDriveConnectorAdapter.class));
        for(SlotCarSystemConnector slotCarSystemConnector: list) {
            // there exists an lib42Connector with the same carID so remove
            if(((AnalogueDriveConnector) slotCarSystemConnector.getDriveConnector()).getLane() == ((AnalogueDriveConnector) analogueConnector
                    .getDriveConnector()).getLane()) {
                removeInputDevice(slotCarSystemConnector.getId());
            }
        }
    }

    /**
     * Removes an existing bluerider connector from the management
     * 
     * @param blueriderConnector
     */
    private void reconnectBlueriderConnector(SlotCarSystemConnector blueriderConnector) {
        // get the test adapters
        List<SlotCarSystemConnector> list = getByDriveConnectorType(TestBlueriderDriveConnectorAdapter.class);
        // get the real adapters
        list.addAll(getByDriveConnectorType(BlueriderDriveConnectorAdapter.class));
        if(list.size() == 1) {
            removeInputDevice(list.get(0).getId());
        }
    }

    /**
     * Removes existing lib42 connectors with the same carID as given lib42Connector.
     * 
     * @param lib42Connector
     */
    private void reconnectLib42Connector(SlotCarSystemConnector lib42Connector) {

        // get the test adapters
        List<SlotCarSystemConnector> list = getByDriveConnectorType(TestLib42DriveConnectorAdapter.class);
        // get the real adapters
        list.addAll(getByDriveConnectorType(Lib42DriveConnectorAdapter.class));
        for(SlotCarSystemConnector slotCarSystemConnector: list) {
            // there exists an lib42Connector with the same carID so remove
            if(((Lib42DriveConnector) slotCarSystemConnector.getDriveConnector()).getCarID() == ((Lib42DriveConnector) lib42Connector
                    .getDriveConnector()).getCarID()) {
                removeInputDevice(slotCarSystemConnector.getId());
            }
        }
    }

    /**
     * Lets the car move a bit to let the user see that the connector works fine
     * 
     * @param connector
     */
    public void testSystemConnector(SlotCarSystemConnector connector) {
        connector.setSpeed(30);
        try {
            Thread.currentThread();
            Thread.sleep(600);
        } catch(InterruptedException e) {
            logger.error("something went wrong during thread sleep", e);
        }
        connector.setSpeed(0);
    }
}
