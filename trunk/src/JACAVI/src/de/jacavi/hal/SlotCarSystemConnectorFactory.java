package de.jacavi.hal;

import java.util.Arrays;

import de.jacavi.hal.analogue.AnalogueConnectorAdapter;
import de.jacavi.hal.bluerider.BlueriderConnectorAdapter;
import de.jacavi.hal.lib42.Lib42ConnectorAdapter;
import de.jacavi.rcp.util.Check;



public class SlotCarSystemConnectorFactory {

    public SlotCarSystemConnector createSlotCarSystemConnector(SlotCarSystemType type) {
        Check.Require(Arrays.asList(SlotCarSystemType.values()).contains(type),
                "type must be an value of SlotCarSystemTypes");
        SlotCarSystemConnector halController = null;
        switch(type) {
            case lib42:

                halController = new Lib42ConnectorAdapter();
                break;
            case bluerider:
                halController = new BlueriderConnectorAdapter();
                break;
            case analogue:
                halController = new AnalogueConnectorAdapter();
                break;
            case none:
                halController = new NoneConnectorAdapter();
                break;
        }

        return halController;
    }
}
