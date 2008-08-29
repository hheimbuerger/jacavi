package de.jacavi.hal;

import de.jacavi.hal.lib42.Lib42DriveConnectorAdapter;
import de.jacavi.hal.lib42.Lib42FeedbackConnectorAdapter;



public class SlotCarSystemConnectorFactory {

    /*
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
    */

    public SlotCarSystemConnector createLib42Connector(int carID, int speedProgramming) {
        return new SlotCarSystemConnector(new Lib42DriveConnectorAdapter(carID), new Lib42FeedbackConnectorAdapter(
                carID));
    }
}
