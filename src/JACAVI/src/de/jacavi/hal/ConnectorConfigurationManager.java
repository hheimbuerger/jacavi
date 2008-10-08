package de.jacavi.hal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.hal.analogue.AnalogueDriveConnector;
import de.jacavi.hal.bluerider.BlueriderDriveConnector;
import de.jacavi.hal.lib42.Lib42DriveConnector;
import de.jacavi.hal.simulation.SimulationDriveConnector;



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

    private final ConnectorFactory connectorFactory;

    private final int numberOfSimulatedConnectors;

    private final Map<UUID, SlotCarSystemConnector> connectors = new TreeMap<UUID, SlotCarSystemConnector>();

    public ConnectorConfigurationManager(ConnectorFactory connectorFactory, int numberOfSimulatedConnectors) {
        logger.info("instantiating ConnectorConfigurationManager.");
        this.connectorFactory = connectorFactory;
        this.numberOfSimulatedConnectors = numberOfSimulatedConnectors;
    }

    /**
     * adds an {@link SlotCarSystemConnector} to the managment.
     * 
     * @param connector
     */
    public void addConnector(SlotCarSystemConnector connector) {

        // logger.info(connector.getName() + " is now under connector management");

        List<Class<?>> connectorsInterfaces = Arrays.asList(connector.getDriveConnector().getClass().getInterfaces());

        if(connectorsInterfaces.contains(Lib42DriveConnector.class)) {
            reconnectLib42Connector(connector);
        } else if(connectorsInterfaces.contains(BlueriderDriveConnector.class)) {
            reconnectBlueriderConnector(connector);
        } else if(connectorsInterfaces.contains(AnalogueDriveConnector.class)) {
            reconnectAnalogueConnector(connector);
        } else if(connectorsInterfaces.contains(SimulationDriveConnector.class)) {
            reconnectSimulationConnector(connector);
        }

        connectors.put(connector.getId(), connector);
    }

    /**
     * Initialize default connectors here.
     * <p>
     * On spring bean creation this method is called
     */
    protected void createDefaultConnectors() {
        // create one simulated connector (dummy)
        for(int i = 1; i <= numberOfSimulatedConnectors; i++) {
            SlotCarSystemConnector simulatedConnector = connectorFactory.createSimulatedConnector("Simulation " + i);
            addConnector(simulatedConnector);
        }
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
     * Get a sorted list of unused (not assigned to any existing player),configured SlotCarSystemConnectors If
     * player==null you will get all unused if player!=null you will get all unused + the one of the player
     * 
     * @param player
     *            the except player
     *            <p>
     * @return a sorted list of SlotCarSystemConnectors
     */
    @SuppressWarnings("unchecked")
    public List<SlotCarSystemConnector> getUnusedConnectors(Player player) {
        List<SlotCarSystemConnector> result = getConnectors();
        List<Player> players = (List<Player>) ContextLoader.getBean("playersBean");
        for(Player p: players) {
            if(p != player) {
                if(p.getSlotCarSystemConnector() != null)

                    result.remove(p.getSlotCarSystemConnector());
            }
        }
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
     * @return a sorted list with {@link SlotCarSystemConnector}s with interface class type
     */
    public List<SlotCarSystemConnector> getByDriveConnectorInterface(Class<?> type) {
        List<SlotCarSystemConnector> result = new ArrayList<SlotCarSystemConnector>();
        for(SlotCarSystemConnector connector: connectors.values()) {
            List<Class<?>> interfaces = Arrays.asList(connector.getDriveConnector().getClass().getInterfaces());
            if(interfaces.contains(type)) {
                result.add(connector);
            }
        }
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
        // get the connectors
        List<SlotCarSystemConnector> list = getByDriveConnectorInterface(AnalogueDriveConnector.class);
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
        // get the connectors
        List<SlotCarSystemConnector> list = getByDriveConnectorInterface(BlueriderDriveConnector.class);
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

        // get the connectors
        List<SlotCarSystemConnector> list = getByDriveConnectorInterface(Lib42DriveConnector.class);
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
        connector.setThrust(30);
        try {
            Thread.currentThread();
            Thread.sleep(600);
        } catch(InterruptedException e) {
            logger.error("something went wrong during thread sleep", e);
        }
        connector.setThrust(0);
    }
}
