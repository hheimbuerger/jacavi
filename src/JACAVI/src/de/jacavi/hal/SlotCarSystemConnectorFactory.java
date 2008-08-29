package de.jacavi.hal;

import java.util.Arrays;

import de.jacavi.hal.analogue.AnalogueDriveConnectorAdapter;
import de.jacavi.hal.analogue.AnalogueFeedbackConnectorAdapter;
import de.jacavi.hal.bluerider.BlueriderDriveConnectorAdapter;
import de.jacavi.hal.lib42.Lib42DriveConnectorAdapter;
import de.jacavi.hal.lib42.Lib42FeedbackConnectorAdapter;
import de.jacavi.hal.simulation.SimulationDriveConnectorAdapter;
import de.jacavi.hal.simulation.SimulationFeddbackConnectorAdapter;
import de.jacavi.rcp.util.Check;



public class SlotCarSystemConnectorFactory {

    public SlotCarSystemConnector createSlotCarSystemConnector(SlotCarSystemType type) {
        Check.Require(Arrays.asList(SlotCarSystemType.values()).contains(type),
                "type must be an value of SlotCarSystemTypes");
        SlotCarSystemConnector systemConnector = null;
        switch(type) {
            case lib42:
                systemConnector = new SlotCarSystemConnector(new Lib42DriveConnectorAdapter(),
                        new Lib42FeedbackConnectorAdapter());
                break;
            case bluerider:
                systemConnector = new SlotCarSystemConnector(new BlueriderDriveConnectorAdapter(),
                        new AnalogueFeedbackConnectorAdapter());
                break;
            case analogue:
                systemConnector = new SlotCarSystemConnector(new AnalogueDriveConnectorAdapter(),
                        new AnalogueFeedbackConnectorAdapter());
                break;
            case simulation:
                systemConnector = new SlotCarSystemConnector(new SimulationDriveConnectorAdapter(),
                        new SimulationFeddbackConnectorAdapter());
                break;
        }

        return systemConnector;
    }
}
