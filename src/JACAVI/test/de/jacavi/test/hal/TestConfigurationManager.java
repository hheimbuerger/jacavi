package de.jacavi.test.hal;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.jacavi.hal.ConnectorConfigurationManager;
import de.jacavi.hal.SlotCarSystemConnector;
import de.jacavi.hal.SlotCarSystemConnectorFactory;



public class TestConfigurationManager {

    private ConnectorConfigurationManager connectorManager = null;

    private SlotCarSystemConnectorFactory factory = null;

    @Before
    public void setUp() throws Exception {
        connectorManager = new ConnectorConfigurationManager();
        factory = new SlotCarSystemConnectorFactory();
    }

    /**
     * @author Florian Roth
     */

    @Test
    public void testAddConnector() {

        SlotCarSystemConnector connector = factory.createSimulatedConnector("simulation test 1");
        UUID id = connector.getId();
        connectorManager.addConnector(connector);
        SlotCarSystemConnector ref = connectorManager.getConnector(id);
        Assert.assertNotNull("connector should not be null", ref);
        Assert.assertEquals(id, ref.getId());
        Assert.assertEquals(1, connectorManager.getConnectors().size());
    }

    @Test
    public void testGetConnectors() {
        for(int i = 0; i < 10; i++) {
            connectorManager.addConnector(factory.createSimulatedConnector("Testconnector:" + i));
        }
        Assert.assertEquals(10, connectorManager.getConnectors().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveInputDevice() {
        for(int i = 0; i < 10; i++) {
            connectorManager.addConnector(factory.createSimulatedConnector("Testconnector:" + i));
        }
        SlotCarSystemConnector conn = factory.createSimulatedConnector("TestConnector to remove");
        UUID id = conn.getId();
        connectorManager.removeInputDevice(id);
        Assert.assertEquals(10, connectorManager.getConnectors().size());
        try {
            connectorManager.getConnector(id);
            Assert.fail("UUID: " + id + " was removed and should not be there");
        } catch(Exception e) {

            // TODO: handle exception
        }
    }

    @Test
    public void testIsIdValid() {
        SlotCarSystemConnector connector = factory.createSimulatedConnector("simulation test 1");
        UUID id = connector.getId();
        connectorManager.addConnector(connector);
        UUID id2 = UUID.randomUUID();
        Assert.assertTrue(connectorManager.isIdValid(id));
        Assert.assertFalse(connectorManager.isIdValid(id2));
    }

    @Test
    public void testGetConnector() {
        SlotCarSystemConnector connector = factory.createSimulatedConnector("simulation test 1");
        UUID id = connector.getId();
        connectorManager.addConnector(connector);

        SlotCarSystemConnector newCOn = connectorManager.getConnector(id);
        Assert.assertEquals(connector, newCOn);
    }

}
